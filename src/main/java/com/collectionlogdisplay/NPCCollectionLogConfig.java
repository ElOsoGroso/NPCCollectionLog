/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.collectionlogdisplay;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(NPCCollectionLogConfig.MAIN_GROUP)
public interface NPCCollectionLogConfig extends Config
{
	String MAIN_GROUP = "npclog";


//	@Alpha
//	@ConfigItem(
//			keyName = "itemBackgroundColor",
//			name = "Background color",
//			description = "Configures the color of the non-collection-log items"
//	)
//	default Color itemBackgroundColor()
//	{
//		return Color.BLACK;
//	}

	@Alpha
	@ConfigItem(
			keyName = "collectionLogObtainedColor",
			name = "Collection logged",
			description = "Sets the color that appears on items that you have obtained from collection log"
	)
	default Color collectionLogObtainedColor()
	{
		return Color.GREEN;
	}
	@Alpha
	@ConfigItem(
			keyName = "collectionLogMissingColor",
			name = "Collection missing",
			description = "Sets the color that appears on items that you have not obtained from collection log"
	)
	default Color collectionLogMissingColor()
	{
		return Color.RED;
	}
	@Alpha
	@ConfigItem(
			keyName = "bankLogObtainedColor",
			name = "Bank obtained",
			description = "Sets the color that appears on normal items that you have banked"
	)
	default Color bankLogObtainedColor()
	{
		return Color.GREEN;
	}
	@Alpha
	@ConfigItem(
			keyName = "bankLogMissingColor",
			name = "Bank missing",
			description = "Sets the color that appears on normal items that you have not banked"
	)
	default Color bankLogMissingColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "showBankLog",
			name = "Display bank collection panel",
			description = "Shows or hides the non collection drops you may want to collect"
	)
	default boolean showBankLog()
	{
		return true;
	}
}
