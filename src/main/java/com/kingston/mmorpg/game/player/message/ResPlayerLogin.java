package com.kingston.mmorpg.game.player.message;

import com.kingston.mmorpg.framework.net.socket.annotation.MessageMeta;
import com.kingston.mmorpg.framework.net.socket.message.Message;
import com.kingston.mmorpg.game.Modules;
import com.kingston.mmorpg.game.player.service.PlayerService;

@MessageMeta(module = Modules.PLAYER, cmd = PlayerService.CMD_RES_LOGIN)
public class ResPlayerLogin extends Message {

}
