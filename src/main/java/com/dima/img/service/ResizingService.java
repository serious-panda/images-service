package com.dima.img.service;

import java.awt.image.BufferedImage;

public interface ResizingService {
	BufferedImage resize(BufferedImage img, int width, int height);
}
