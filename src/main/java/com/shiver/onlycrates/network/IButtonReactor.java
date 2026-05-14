package com.shiver.onlycrates.network;

import net.minecraft.entity.player.EntityPlayer;

public interface IButtonReactor {
    void onButtonPressed(int buttonID, EntityPlayer player);
}
