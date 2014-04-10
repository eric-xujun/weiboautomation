package weibo4j.apps.resource;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.wink.common.internal.utils.MediaTypeUtils;
import org.apache.wink.common.model.multipart.InMultiPart;
import org.apache.wink.common.model.multipart.InPart;

import weibo4j.apps.excel.ExcelParser;

@Path("/upload")
public class AttachmentResource {

	@POST
    @Consumes( MediaTypeUtils.MULTIPART_FORM_DATA)
    public void attachImage(InMultiPart inMP) throws IOException {  
		while(inMP.hasNext()) {
			InPart part = inMP.next();
			InputStream is = part.getBody(InputStream.class,null);
			HSSFWorkbook excel = new HSSFWorkbook(is);
			ExcelParser parser = new ExcelParser();
			parser.parse(excel);
		}
    }
}
