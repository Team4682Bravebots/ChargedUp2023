package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.common.VectorUtils;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.NavxSubsystem;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;

import java.lang.Math;

/**
 * Implements a command to perform a single step of an auto balancing routine. 
 * Intended to be called in a loop like this:
 * AutoBalanceStepCommand(drive, navx).repeatedly().until(navx::isLevel)
 */
public class AutoBalanceStepCommand extends CommandBase{
  private Timer driveTimer = new Timer();
  private Timer waitTimer = new Timer();
  private boolean done = false;
  private double xVelocity = 0.0;
  private double yVelocity = 0.0;
  private double rotVelocity = 0.0;
  // wait duration needs to be sufficiently long to allow the ramp to settle for the navx to take another reading
  private double waitDurationSecondsValue = .5;
  // TODO hardcoded values eventually replaced when we implement PID controller drive command
  private double driveDurationSecondsValue = 0.25;  
  private double velocityValue = 0.4;


  NavxSubsystem navxsubsystem = null; 
  DrivetrainSubsystem drivetrainsubsystem = null;

  /**
   * A constructor for auto balance step command
   * @param drivetrainSubsystem
   * @param navxsubsystem
   */
  public AutoBalanceStepCommand(DrivetrainSubsystem drivetrainSubsystem, NavxSubsystem navxsubsystem) {
    System.out.println("constructing auto balance step command");
    this.navxsubsystem = navxsubsystem;
    this.drivetrainsubsystem = drivetrainSubsystem;

    // do not need to add Navx as a requirement because it is read-only
    addRequirements(drivetrainSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("initializaing auto balance step command");
    Translation2d angleOfSteepestAscent = VectorUtils.getAngleOfSteepestAscent(navxsubsystem.getEulerAngle());
    Translation2d velocityVec = normalizeXYVelocities(angleOfSteepestAscent);
    xVelocity = velocityVec.getX();
    yVelocity = velocityVec.getY();

    drivetrainsubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    driveTimer.reset();
    waitTimer.reset();
    driveTimer.start();
    done = false;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute()
  {

    // drive time interval followed by wait time interval
    if (driveTimer.hasElapsed(this.driveDurationSecondsValue))
    {
      drivetrainsubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
      waitTimer.start();
    }
    // drive along the vector of steepest ascent
    else {
      drivetrainsubsystem.drive(new ChassisSpeeds(xVelocity, yVelocity, rotVelocity));
    }

    if (waitTimer.hasElapsed(this.waitDurationSecondsValue))
    {
      System.out.println("auto balance step command: completed one cycle");
      System.out.println("RecentPitches " + navxsubsystem.getRecentPitches());
      System.out.println("RecentRolls " + navxsubsystem.getRecentRolls());
      done = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted)
  {
    drivetrainsubsystem.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    if(interrupted)
    {
      done = true;      
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished()
  {
    return done;
  }

  /**
   * Scales XY values to the desired velocity 
   * @param vec - Translation2d vector to be scaled
   * @return - Translation2d scaled vector
   */
  private Translation2d normalizeXYVelocities(Translation2d vec)
  {
    double h = Math.sqrt(Math.pow(vec.getX(), 2) + Math.pow(vec.getY(), 2));
    return new Translation2d((vec.getX()/h) * velocityValue, (vec.getY()/h) * velocityValue);
  }


}
