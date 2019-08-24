package de.pt400c.defaultsettings;

import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class FramebufferPopup {
	
	public final int width;
	public final int height;
	public int msColorRenderBuffer;
	public int msFbo;
    public int texture;
    public int fbo;
	   
	public FramebufferPopup(int width, int height) {
		this.width = width;
		this.height = height;
		setupFBO();
	}

	public void setupFBO() {
        msColorRenderBuffer = glGenRenderbuffers();
        msFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, msFbo);
        glBindRenderbuffer(GL_RENDERBUFFER, msColorRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, 9, GL_RGBA8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, msColorRenderBuffer);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        texture = glGenTextures();
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}