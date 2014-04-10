package weibo4j.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import weibo4j.model.Constants;
import weibo4j.model.WeiboException;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.Image.Format;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.png.PNGImageReader;

/**
 * 临时存储上传图片的内容，格式，文件信息等
 * 
 */
public class ImageItem {
	private byte[] content;
	private String name;
	private String contentType;
	public ImageItem(byte[] content) throws WeiboException, IOException {
	    this(Constants.UPLOAD_MODE,content);
	}
	public ImageItem(String name,byte[] content) throws WeiboException, IOException{
		String imgtype = null;
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		Image image = ImagesServiceFactory.makeImage(content);
		Format format = image.getFormat();
		imgtype = format.name();
		//imgtype = getContentType(content);
		
	    if(imgtype!=null&&(imgtype.equalsIgnoreCase("GIF")||imgtype.equalsIgnoreCase("PNG")
	            ||imgtype.equalsIgnoreCase("JPEG"))){
	    	this.content=content;
	    	this.name=name;
	    	this.contentType=imgtype;
	    }else{
	    	throw new WeiboException(
            "Unsupported image type, Only Suport JPG ,GIF,PNG!");
	    }
	}
	
	public byte[] getContent() {
		return content;
	}
	public String getName() {
		return name;
	}
	public String getContentType() {
		return contentType;
	}

	public static String getContentType(byte[] mapObj) throws IOException {

		String type = "";
		ByteArrayInputStream bais = null;
		MemoryCacheImageInputStream mcis = null;
		try {
			bais = new ByteArrayInputStream(mapObj);
			mcis = new MemoryCacheImageInputStream(bais);
			Iterator itr = ImageIO.getImageReaders(mcis);
			while (itr.hasNext()) {
				ImageReader reader = (ImageReader) itr.next();
				if (reader instanceof GIFImageReader) {
					type = "image/gif";
				} else if (reader instanceof JPEGImageReader) {
					type = "image/jpeg";
				} else if (reader instanceof PNGImageReader) {
					type = "image/png";
				} else if (reader instanceof BMPImageReader) {
					type = "application/x-bmp";
				}
			}
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException ioe) {

				}
			}
			if (mcis != null) {
				try {
					mcis.close();
				} catch (IOException ioe) {

				}
			}
		}
		return type;
	}
}
