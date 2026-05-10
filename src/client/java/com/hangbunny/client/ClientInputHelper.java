package com.hangbunny.client;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;

public class ClientInputHelper {
    public static boolean hasShiftDown() {
        long windowHandle = Minecraft.getInstance().getWindow().handle();
        boolean isShiftDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS 
                            || GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

        return isShiftDown;
    }
}