/*
 *    Copyright 2025 vatten <vatten.dev>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package dev.vatten.baserad.configs;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
public class PluginConfig {
    @Getter
    @Comment({
            "Whether a player can set a bounty on themselves."
    })
    private boolean shouldSelfBounty = true;

    @Getter
    @Comment({
            "Whether the bounty setter should be able to claim a bounty they set."
    })
    private boolean shouldSetterClaim = true;
    // TODO: set all of these to false before publish
    @Getter
    @Comment({
            "Whether the bounty target should be able to claim a bounty set on them."
    })
    private boolean shouldTargetClaim = true;

    @Getter
    @Comment({
            "Above which threshold (number of hunters) a bounty is considered 'hot'."
    })
    private int hotBountyThreshold = 5;
}