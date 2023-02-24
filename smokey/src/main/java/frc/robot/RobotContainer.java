// ************************************************************
// Bishop Blanchet Robotics
// Home of the Cybears
// FRC - Charged Up - 2023
// File: RobotContainer.java
// Intent: Forms key glue class to land subsystems and input devices that will themselves create commands applied to the robot.
// ************************************************************

// ʕ •ᴥ•ʔ ʕ•ᴥ•  ʔ ʕ  •ᴥ•ʔ ʕ •`ᴥ´•ʔ ʕ° •° ʔ ʕ •ᴥ•ʔ ʕ•ᴥ•  ʔ ʕ  •ᴥ•ʔ ʕ •`ᴥ´•ʔ ʕ° •° ʔ 

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.DefaultArmCommand;
import frc.robot.commands.DefaultDriveCommand;
import frc.robot.control.AutonomousChooser;
import frc.robot.control.InstalledHardware;
import frc.robot.control.ManualInputInterfaces;
import frc.robot.control.SubsystemCollection;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.PickerSubsystem;
import frc.robot.subsystems.StabilizerSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {

  private SubsystemCollection subsystems = new SubsystemCollection();
  private final AutonomousChooser autonomousChooser; 

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {

    // init the various subsystems
    this.initializeDrivetrainSubsystem();
    this.initializeArmSubsystem();
    this.initializePickerSubsystem();
    this.initializeStablizerSubsystem();

    // init the input system 
    this.initializeManualInputInterfaces();

    // calculate and update the current position of the robot
    this.calculateAndUpdateRobotPosition();

    // Configure the button bindings
    System.out.println(">>>> Initializing button bindings.");
    this.subsystems.getManualInputInterfaces().initializeButtonCommandBindings();
    System.out.println(">>>> Finished initializing button bindings.");

    this.autonomousChooser = new AutonomousChooser(subsystems);

  }
 
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return autonomousChooser.getCommand();
  }

  /**
   * A method to init the input interfaces
   */
  private void initializeManualInputInterfaces() {
    // note: in this case it is safe to build the interfaces if only one of the controllers is present
    // because button binding assignment code checks that each is installed later (see: initializeButtonCommandBindings)
    if(InstalledHardware.driverXboxControllerInstalled ||
      InstalledHardware.coDriverXboxControllerInstalled)
    {
      subsystems.setManualInputInterfaces(new ManualInputInterfaces(subsystems));
      System.out.println("SUCCESS: initializeManualInputInterfaces");
    }
    else
    {
      System.out.println("FAIL: initializeManualInputInterfaces");
    }
  }

  /**
   * A method to init the drive train
   */
  private void initializeDrivetrainSubsystem() {
    if(InstalledHardware.leftFrontDriveInstalled && 
      InstalledHardware.leftRearDriveInstalled && 
      InstalledHardware.rightFrontDriveInstalled &&
      InstalledHardware.rightRearDriveInstalled &&
      InstalledHardware.navxInstalled)
    {
      // The robot's subsystems and commands are defined here...
      subsystems.setDriveTrainSubsystem(new DrivetrainSubsystem());
      System.out.println("SUCCESS: initializeDrivetrain");

      // Set up the default command for the drivetrain.
      // The controls are for field-oriented driving:
      // Left stick Y axis -> forward and backwards movement
      // Left stick X axis -> left and right movement
      // Right stick X axis -> rotation
      subsystems.getDriveTrainSubsystem().setDefaultCommand(new DefaultDriveCommand(
        subsystems.getDriveTrainSubsystem(),
        () -> -modifyAxis(subsystems.getManualInputInterfaces().getInputArcadeDriveY()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
        () -> -modifyAxis(subsystems.getManualInputInterfaces().getInputArcadeDriveX()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
        () -> -modifyAxis(subsystems.getManualInputInterfaces().getInputSpinDriveX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND
      ));
    }
    else
    {
      System.out.println("FAIL: initializeDrivetrain");
    }
  }
  
  /**
   * A method to init the arm
   */
  private void initializeArmSubsystem() {
    if(InstalledHardware.verticalArmMotorInstalled && 
      InstalledHardware.horizontalArmMotorInstalled)
    {
      // The robot's subsystems and commands are defined here...
      subsystems.setArmSubsystem(new ArmSubsystem());
      System.out.println("SUCCESS: initializeArm");

      // Set up the default command for the arm.
      // Left stick X axis -> horizontal arm in / out movement
      // Left stick Y axis -> vertical arm in / out movement
      subsystems.getArmSubsystem().setDefaultCommand(new DefaultArmCommand(
        subsystems.getArmSubsystem(),
        () -> subsystems.getManualInputInterfaces().getInputArcadeArmY(),
        () -> subsystems.getManualInputInterfaces().getInputArcadeArmZ()
      ));
    }
    else
    {
      System.out.println("FAIL: initializeArm");
    }
  }

  /**
   * A method to init the picker
   */
  private void initializePickerSubsystem() {
    if(InstalledHardware.pickerPneumaticsInstalled)
    {
      subsystems.setPickerSubsystem(new PickerSubsystem());
      System.out.println("SUCCESS: initializePicker");
    }
    else
    {
      System.out.println("FAIL: initializePicker");
    }
  }

  /**
   * A method to init the stablizer
   */
  private void initializeStablizerSubsystem() {
    if(InstalledHardware.stablizerPneumaticsInstalled)
    {
      subsystems.setStabilizerSubsystem(new StabilizerSubsystem());
      System.out.println("SUCCESS: initializeStablizer");
    }
    else
    {
      System.out.println("FAIL: initializeStablizer");
    }
  }  

  /**
   * A method to calculate the initial position of the robot
   */
  private void calculateAndUpdateRobotPosition() {
    // TODO - do this right!
    Pose2d initialRobotPosition = new Pose2d();
    // TODO - need to implement this when we have vision
    // 1. find the April tag that is closest
    // 2. estimate the robot centroid location
    // 3. find other April tags ...
    // 4. apply some smoothing
    if(subsystems.getDriveTrainSubsystem() != null)
    {
      subsystems.getDriveTrainSubsystem().setRobotPosition(initialRobotPosition);
      System.out.println(">>>> Initialized Robot Position. ");
    }
  }

  private static double deadband(double value, double deadband) {
    if (Math.abs(value) > deadband) {
      if (value > 0.0) {
        return (value - deadband) / (1.0 - deadband);
      } else {
        return (value + deadband) / (1.0 - deadband);
      }
    } else {
      return 0.0;
    }
  }

  private static double modifyAxis(double value) {
    // Deadband
    value = deadband(value, 0.05);

    // Square the axis
    value = Math.copySign(value * value * value * value, value);

    return value;
  }
}