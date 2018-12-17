package info.smartkit.dl4j.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;

import info.smartkit.dl4j.storage.StorageService;
import info.smartkit.dl4j.utils.ImageClassifier;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import info.smartkit.dl4j.dto.JsonObject;
import info.smartkit.dl4j.utils.FileUtil;
import info.smartkit.dl4j.utils.OcrInfoHelper;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * The Class OCRsController.
 */
@RestController
// @see: http://spring.io/guides/gs/reactor-thumbnailer/
@RequestMapping(value = "/dl4j")
public class DL4JController {
	//
	private static Logger LOG = LogManager.getLogger(DL4JController.class);

	// Enum for image size.
	enum ImageSize {
		ori, sml, ico
	}

	private final StorageService storageService;
	private ImageClassifier imageClassifier;

	@Autowired
	public DL4JController(StorageService storageService, ImageClassifier imageClassifier) {
		this.storageService = storageService;
		this.imageClassifier = imageClassifier;
	}

	// @see: https://spring.io/guides/gs/uploading-files/
	@RequestMapping(method = RequestMethod.POST, value = "/dl4j", consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Response a string describing DL4J' picture is successfully uploaded or not.")
//	@ApiImplicitParams({@ApiImplicitParam(name="Authorization", value="Authorization DESCRIPTION")})
	public @ResponseBody JsonObject DL4JFileUpload(
			@RequestParam(value = "model", required = false, defaultValue =
					"vgg16") String model,
			@RequestPart(value = "file") @Valid @NotNull @NotBlank MultipartFile file) {
		// @Validated MultipartFileWrapper file, BindingResult result, Principal
		// principal){
		String predictions = "Error";
		if (!file.isEmpty()) {
			// DL4Jing:
			storageService.store(file);
			try {
				predictions = imageClassifier.classify(file.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			LOG.error("You failed to upload " + file.getName() + " because the file was empty.");
		}
		return new JsonObject(predictions);
	}

	//
	@SuppressWarnings("unused")
	private String thumbnailImage(int width, int height, String source)
			throws IOException, InterruptedException, IM4JavaException {
		//
		String small4dbBase = FilenameUtils.getBaseName(source) + "_" + String.valueOf(width) + "x"
				+ String.valueOf(height) + "." + FilenameUtils.getExtension(source);
		String small4db = FileUtil.getUploads() + small4dbBase;
		String small = getClassPath() + small4db;
		// @see:
		// http://paxcel.net/blog/java-thumbnail-generator-imagescalar-vs-imagemagic/
		ConvertCmd cmd = new ConvertCmd();
		// cmd.setSearchPath("");
		File thumbnailFile = new File(small);
		if (!thumbnailFile.exists()) {
			IMOperation op = new IMOperation();
			op.addImage(source);
			op.thumbnail(width);
			op.addImage(small);
			cmd.run(op);
			LOG.info("ImageMagick success result:" + small);
		}
		return small4dbBase;
	}

	// @Autowired
	// private FolderSetting folderSetting;

	private Map<String, String> fileOperation(MultipartFile file) {
		Map<String, String> _imageMagickOutput = new HashMap<String, String>();
		String dbFileName = null;
		String fullFileName = null;
		try {
			byte[] bytes = file.getBytes();
			String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
			String fileNameAppendix
			// = "temp" + "." + fileExt;
			= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + "." + fileExt;

			dbFileName = FileUtil.getUploads() + fileNameAppendix;
			fullFileName = dbFileName;

			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fullFileName)));
			stream.write(bytes);
			stream.close();
			// System.out.println("Upload file success." + fullFileName);
			LOG.info("Upload file success." + fullFileName);
			// ImageMagick convert options; @see:
			// http://paxcel.net/blog/java-thumbnail-generator-imagescalar-vs-imagemagic/
			_imageMagickOutput.put(ImageSize.ori.toString(), fileNameAppendix);
			// _imageMagickOutput.put(ImageSize.sml.toString(),
			// thumbnailImage(150, 150, fullFileName));
			// _imageMagickOutput.put(ImageSize.ico.toString(),
			// thumbnailImage(32, 32, fullFileName));
			return _imageMagickOutput;
		} catch (Exception e) {
			// System.out.println("You failed to convert " + fullFileName + " =>
			// " + e.toString());
			LOG.error("You failed to convert " + fullFileName + " => " + e.toString());
		}
		return _imageMagickOutput;
	}

	public String getClassPath() {
		String classPath = this.getClass().getResource("/").getPath();
		return classPath;
	}
}
