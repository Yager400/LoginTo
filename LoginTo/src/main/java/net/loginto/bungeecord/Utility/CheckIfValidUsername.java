/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utility;

import static net.loginto.bungeecord.Utility.FileMGR.YamlRead;

public class CheckIfValidUsername {
    public static boolean checkValidUsername(String username) {
        String bedrock_prefix = YamlRead("user-auth.bedrock-prefix").trim();
        
        if (username.matches("^[a-zA-Z0-9_" + bedrock_prefix + "]{3,16}$")) {
            return true;
        }
  
        return false;
    }
}
