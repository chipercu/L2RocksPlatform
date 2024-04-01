package com.fuzzy.subsystem.gameserver.cache;

import gnu.trove.map.hash.TIntObjectHashMap;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.idfactory.IdFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ImagesChache {
    private static final Logger _log = Logger.getLogger(ImagesChache.class.getName());
    private static final int[] SIZES = new int[]{1, 2, 4, 8, 12, 16, 32, 64, 128, 256, 512, 1024};
    private static final int MAX_SIZE = SIZES[SIZES.length - 1];

    public static final Pattern HTML_PATTERN = Pattern.compile("%img:(.*?)%", Pattern.DOTALL);

    private final static ImagesChache _instance = new ImagesChache();

    public final static ImagesChache getInstance() {
        return _instance;
    }

    private final Map<String, Integer> _imagesId = new HashMap<String, Integer>();
    /**
     * Получение изображения по ID
     */
    private final TIntObjectHashMap<byte[]> _images = new TIntObjectHashMap<byte[]>();

    private ImagesChache() {
        load();
    }

    public void load() {
        _imagesId.clear();
        _images.clear();
        //_log.info("ImagesChache: Loading images...");

        File dir = new File(ConfigValue.DatapackRoot, "images");
        if (!dir.exists() || !dir.isDirectory()) {
            //_log.info("ImagesChache: Files missing, loading aborted.");
            return;
        }

        int count = 0;
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                continue;

            File files = resizeImage(file);
            if (file == null) {
                _log.warning("ImagesChache: Error while resizeImage " + file.getName() + " image.");
                ;
                continue;
            }

            count++;

            String fileName = file.getName();
            try {
                ByteBuffer bf = DDSConverter.convertToDDS(file);
                byte[] image = bf.array();
                int imageId = IdFactory.getInstance().getNextId();

                _imagesId.put(fileName.toLowerCase(), imageId);
                _images.put(imageId, image);

                _log.info("ImagesChache: Loaded " + fileName + " image.");
            } catch (IOException ioe) {
                _log.warning("ImagesChache: Error while loading " + fileName + " image.");
                ;
            }
        }

        _log.info("ImagesChache: Loaded " + count + " images");
    }

    private static File resizeImage(File file) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException ioe) {
            _log.warning("ImagesChache: Error while resizing " + file.getName() + " image.");
            return null;
        }

        if (image == null)
            return null;

		/*int width = image.getWidth();
		int height = image.getHeight();

		boolean resizeWidth = true;
		if(width > MAX_SIZE)
		{
			image = image.getSubimage(0, 0, MAX_SIZE, height);
			resizeWidth = false;
		}

		boolean resizeHeight = true;
		if(height > MAX_SIZE)
		{
			image = image.getSubimage(0, 0, width, MAX_SIZE);
			resizeHeight = false;
		}

		int resizedWidth = width;
		if(resizeWidth)
		{
			for(int size : SIZES)
			{
				if(size < width)
					continue;

				resizedWidth = size;
				break;
			}
		}

		int resizedHeight = height;
		if(resizeHeight)
		{
			for(int size : SIZES)
			{
				if(size < height)
					continue;

				resizedHeight = size;
				break;
			}
		}

		if(resizedWidth != width || resizedHeight != height)
		{
			for(int x = 0; x < resizedWidth; x++)
			{
				for(int y = 0; y < resizedHeight; y++)
				{
					image.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
			String filename = file.getName();
			String format = filename.substring(filename.lastIndexOf("."));
			try
			{
				ImageIO.write(image, format, file);
			}
			catch(IOException ioe)
			{
				_log.warning("ImagesChache: Error while resizing " + file.getName() + " image.");
				return null;
			}
		}*/
        return file;
    }

    public int getImageId(String val) {
        int imageId = 0;
        if (_imagesId.get(val.toLowerCase()) != null)
            imageId = _imagesId.get(val.toLowerCase());
        return imageId;
    }

    public byte[] getImage(int imageId) {
        return _images.get(imageId);
    }
}