/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.velocity.Utility;

import static net.loginto.velocity.Utility.FileMGR.YamlRead;
import com.velocitypowered.api.event.connection.PreLoginEvent;

public class CheckIfValidUsername {
    public static Boolean checkValidUsername(PreLoginEvent event) {

        String bedrock_prefix = YamlRead("user-auth.bedrock-prefix").trim();
        
        if (event.getUsername().matches("^[a-zA-Z0-9_" + bedrock_prefix + "]{3,16}$")) {
            return true;
        }
  
        return false;
    }
}
