package frc.robot.control;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.common.SwerveTrajectoryGenerator;
import frc.robot.subsystems.DrivetrainSubsystem;
import java.util.ArrayList;

public class Trajectories {
    public Pose2d Node1Position;
    public Pose2d Node2Position;
    public Pose2d Node5Position;
    public Pose2d Node8Position;
    public Pose2d Node9Position;
    public Pose2d InfrontOfRampPosition;
    public Trajectory LeftTrajectory;
    public Trajectory Node2Trajectory;
    public Trajectory RightTrajectory;
    public Trajectory Node8Trajectory;
    public Trajectory MiddleTrajectory;
    public Trajectory LeftToOntoRampTrajectory;
    public Trajectory RightToOntoRampTrajectory;
    public Trajectory BehindToOntoRampTrajectory;
    public Trajectory DirectToRampTrajectory;
    
    public Trajectories(DrivetrainSubsystem drivetrain){

        TrajectoryConfig config = drivetrain.getTrajectoryConfig();
        // trajectory config with a fast starting velocity for ramp driving. 
        // have to get a new config so that changes to this one don't affect the original
        TrajectoryConfig fastConfig = drivetrain.getTrajectoryConfig();
        fastConfig.setStartVelocity(fastConfig.getMaxVelocity() * 0.65); // less than max speed

        this.Node1Position = new Pose2d(1.678, 4.994, Rotation2d.fromDegrees(180));
        this.Node2Position = new Pose2d(1.678, 4.433, Rotation2d.fromDegrees(180));
        this.Node5Position = new Pose2d(1.678, 2.750, Rotation2d.fromDegrees(180));
        this.Node8Position = new Pose2d(1.678, 1.067, Rotation2d.fromDegrees(180));
        this.Node9Position = new Pose2d(1.678, 0.506, Rotation2d.fromDegrees(180));
        this.InfrontOfRampPosition = new Pose2d(2.2, 2.75, Rotation2d.fromDegrees(90));
        
        // we need two different ramp waypoints.  There is slippage getting onto ramp, so 
        // we need to overshoot the center to ensure the robot gets far enough onto the ramp.   
        Pose2d RampFarWaypoint = new Pose2d(4.122, 2.748, Rotation2d.fromDegrees(90));
        Pose2d RampNearWaypoint = new Pose2d(3.54, 2.748, Rotation2d.fromDegrees(90));
        Pose2d BehindTrajectoryEndPosition = new Pose2d(5.07, 2.748, Rotation2d.fromDegrees(90));
 
        // Left waypoints drive from Node 1 or 2 to a location out of the community
        ArrayList<Translation2d> LeftWaypoints = new ArrayList<Translation2d>();
        LeftWaypoints.add(new Translation2d(2.1, 4.67));
        LeftWaypoints.add(new Translation2d(3.7, 4.67));
        Pose2d LeftTrajectoryEndPosition = new Pose2d(5.3, 4.67, Rotation2d.fromDegrees(0));
        this.LeftTrajectory = SwerveTrajectoryGenerator.generateTrajectory(Node1Position, LeftWaypoints, LeftTrajectoryEndPosition, config);
        this.Node2Trajectory = SwerveTrajectoryGenerator.generateTrajectory(Node2Position, LeftWaypoints, LeftTrajectoryEndPosition, config);
        
        // Right waypoints drive from Node 8 or 9 to a location out of the community
        ArrayList<Translation2d> RightWaypoints = new ArrayList<Translation2d>();
        RightWaypoints.add(new Translation2d(2.1, .69));
        RightWaypoints.add(new Translation2d(3.7, .69));
        Pose2d RightTrajectoryEndPosition = new Pose2d(5.3, .69, Rotation2d.fromDegrees(0));
        this.RightTrajectory = SwerveTrajectoryGenerator.generateTrajectory(Node9Position, RightWaypoints, RightTrajectoryEndPosition, config);
        this.Node8Trajectory = SwerveTrajectoryGenerator.generateTrajectory(Node8Position, RightWaypoints, RightTrajectoryEndPosition, config);

        // To drive from left or right onto ramp, use a common central waypoint
        ArrayList<Translation2d> BehindToRampWaypoints = new ArrayList<Translation2d>();
        BehindToRampWaypoints.add(new Translation2d(5.81, 2.748));
        this.LeftToOntoRampTrajectory = SwerveTrajectoryGenerator.generateTrajectory(LeftTrajectoryEndPosition, BehindToRampWaypoints, BehindTrajectoryEndPosition, config);
        this.RightToOntoRampTrajectory = SwerveTrajectoryGenerator.generateTrajectory(RightTrajectoryEndPosition, BehindToRampWaypoints, BehindTrajectoryEndPosition, config);

        // TODO we may have to break this up into multiple trajectories to control velocities over the ramp
        ArrayList<Pose2d> MiddleWaypoints = new ArrayList<Pose2d>();
        MiddleWaypoints.add(Node5Position);
        MiddleWaypoints.add(BehindTrajectoryEndPosition);
        this.MiddleTrajectory = SwerveTrajectoryGenerator.generateTrajectory(MiddleWaypoints, config);
        
        // Drive onto ramp from behind
        ArrayList<Pose2d> BehindToOntoRampWaypoints = new ArrayList<Pose2d>();
        BehindToOntoRampWaypoints.add(BehindTrajectoryEndPosition);
        BehindToOntoRampWaypoints.add(RampNearWaypoint);
        // use fastConfig for this trajectory 
        this.BehindToOntoRampTrajectory = SwerveTrajectoryGenerator.generateTrajectory(BehindToOntoRampWaypoints, fastConfig); 

        this.LeftToOntoRampTrajectory.concatenate(BehindToOntoRampTrajectory);
        this.RightToOntoRampTrajectory.concatenate(BehindToOntoRampTrajectory);   

        // Drive onto ramp from in front 
        ArrayList<Pose2d> InfrontToOntoRampWaypoints = new ArrayList<Pose2d>();
        InfrontToOntoRampWaypoints.add(InfrontOfRampPosition);
        InfrontToOntoRampWaypoints.add(RampFarWaypoint);
        // use fastConfig for this trajectory 
        Trajectory InfrontToOntoRampTrajectory = SwerveTrajectoryGenerator.generateTrajectory(InfrontToOntoRampWaypoints, fastConfig);

        ArrayList<Pose2d> Node5ToFrontOfRampWaypoints = new ArrayList<Pose2d>();
        Node5ToFrontOfRampWaypoints.add(Node5Position);
        Node5ToFrontOfRampWaypoints.add(InfrontOfRampPosition);
        Trajectory Node5ToFrontOfRampTrajectory = SwerveTrajectoryGenerator.generateTrajectory(Node5ToFrontOfRampWaypoints, config);
        
        this.DirectToRampTrajectory = Node5ToFrontOfRampTrajectory.concatenate(InfrontToOntoRampTrajectory);
    }
}   
