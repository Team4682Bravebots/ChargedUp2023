/*
 * This file is modified from SwerveDriveSpecialties/swerve-lib/SdsModuleConfigurations.java
 * License https://www.swervedrivespecialties.com/pages/creative-commons-attribution-noncommercial-4-0-international-public-license
 */

package frc.robot.swerveHelpers;

import com.swervedrivespecialties.swervelib.ModuleConfiguration;

public class WcpModuleConfigurations {
    public static final ModuleConfiguration SWERVEX = new ModuleConfiguration(
            0.1016,  //DONE
            (14.0 / 50.0) * (28.0 / 16.0) * (15.0 / 60.0), //TODO
            true, //TODO
            (15.0 / 32.0) * (10.0 / 60.0), //TODO
            true //TODO
    );
}
