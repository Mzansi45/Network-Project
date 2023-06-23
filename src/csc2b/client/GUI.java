package csc2b.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.photo.Photo;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

/**@version PracX Network Project
 * 
 * @author T Gulube 219020988
 *
 */
public class GUI extends HBox{
	private Socket socket = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private BufferedReader br = null;
	private InputStream is = null;
	private BufferedOutputStream bos = null;
	private OutputStream os = null;
	 
	private Image image = null;
	private Text errorMessage = new Text("Error Message");
	private VBox display = new VBox();
	private ImageView imageDisplay = null;
	private File file = null;
	private boolean imageAvailable = false;
	
	// constructor and Graphical user interface initializer
	public GUI()
	{
		// GUI setup
		VBox buttons = new VBox();
		buttons.setBackground(new Background(new BackgroundFill(Color.rgb(94, 90, 90),CornerRadii.EMPTY,Insets.EMPTY)));
		buttons.setPadding(new Insets(10));
		this.setBackground(new Background(new BackgroundFill(Color.rgb(94, 90, 90),CornerRadii.EMPTY,Insets.EMPTY)));
				
		//buttons
		Button chooseFile = new Button("Choose Image");		
		Button grayScale = new Button("Gray Scale");		
		Button rotate = new Button("Rotate Image");
		Button erosion = new Button("Erosion");
		Button dilation = new Button("Dilation");
		Button canny = new Button("Canny");
		Button crop = new Button("Crop");
		Button faces = new Button("Identify Faces");
		Button fastImage = new Button("Fast");
		Button orb = new Button("ORB");
		Button removeEdits = new Button("Remove Edits");
		Button edgePreserving = new Button("Edge Preserving Filter");
		Button detailEnhance = new Button("Detail Enhance");
		Button stylization = new Button("Stylization");
		
		//Photo.stylization(src, src2);
		stylization.setOnAction(event->{
			if(imageAvailable)
			{
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				
				Mat src = Imgcodecs.imread("data/"+file.getName());
				String filename ="data/Stylization/"+file.getName();
				
				Mat src2 = new Mat();
				
				Photo.stylization(src, src2);
				
				Imgcodecs.imwrite(filename, src2);
				try {
					Image image = new Image(new FileInputStream(new File(filename)));
					//Image image = new Image(new FileInputStream(new File("data/FaceDetected/"+file.getName())));
					
					ShowImage(image);
					//imageDisplay.setImage(image);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				errorMessage.setText("Stylization Filter Added");
			}else
			{
				errorMessage.setText("Choose an image first");
			}
		});
		
		//Photo.detailEnhance(src, src2,200);
		detailEnhance.setOnAction(event->{
			if(imageAvailable)
			{
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				
				Mat src = Imgcodecs.imread("data/"+file.getName());
				String filename ="data/DetailEnhance/"+file.getName();
				
				Mat src2 = new Mat();
				
				Photo.detailEnhance(src, src2,150);
				
				Imgcodecs.imwrite(filename, src2);
				try {
					Image image = new Image(new FileInputStream(new File(filename)));
					//Image image = new Image(new FileInputStream(new File("data/FaceDetected/"+file.getName())));
					
					ShowImage(image);
					//imageDisplay.setImage(image);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				errorMessage.setText("Details Enhanced");
			}else
			{
				errorMessage.setText("Choose an image first");
			}
		});
		
		//Photo.edgePreservingFilter(src, src2);
		edgePreserving.setOnAction(event->{
			if(imageAvailable)
			{
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				
				Mat src = Imgcodecs.imread("data/"+file.getName());
				String filename ="data/EdgeEnhanced/"+file.getName();
				
				Mat src2 = new Mat();
				//Photo.fastNlMeansDenoising(src,src2);
				Photo.edgePreservingFilter(src, src2);
				
				Imgcodecs.imwrite(filename, src2);
				try {
					Image image = new Image(new FileInputStream(new File(filename)));
					//Image image = new Image(new FileInputStream(new File("data/FaceDetected/"+file.getName())));
					
					ShowImage(image);
					//imageDisplay.setImage(image);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				errorMessage.setText("Edges Preserved");
			}else
			{
				errorMessage.setText("Choose an image first");
			}
		});
		
		//Button Action events
		chooseFile.setOnAction(event->{
			try {
				FileChooser fc = new FileChooser();
				fc.setInitialDirectory(new File("data"));
				file = null;
				file = fc.showOpenDialog(null);
				
				image = new Image(new FileInputStream(file));
				//image = new Image(new FileInputStream(new File("data","responsebtn.png")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			ShowImage(image);
			imageAvailable = true;
			errorMessage.setText("Image Choosen");
		});
		
		grayScale.setOnAction(event->{
			//apply edits only if there is an image
			if(imageAvailable)
			{
				serverRequest("/api/GrayScale");
				errorMessage.setText("Image Gray Scaled");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}

		});
		
		rotate.setOnAction(event->{
			if(imageAvailable)
			{
				serverRequest("/api/Rotate");
				errorMessage.setText("Image Rotated");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}

		});
		
		erosion.setOnAction(event->{
			if(imageAvailable)
			{
				serverRequest("/api/Erosion");
				errorMessage.setText("Image Eroded");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}
			
			
		});
		
		dilation.setOnAction(event->{
			if(imageAvailable)
			{
				serverRequest("/api/Dilation");
				errorMessage.setText("Image Dilated");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}
			
		
		});
		fastImage.setOnAction(event->{
			if(imageAvailable)
			{
				serverRequest("/api/Fast");
				errorMessage.setText("Fast Edit Applied");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}
			
			
		});
		crop.setOnAction(event->{
			if(imageAvailable)
			{
				serverRequest("/api/Crop");
				errorMessage.setText("Image Cropped");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}
			
		
		});
		canny.setOnAction(event->{
			if(imageAvailable)
			{
				serverRequest("/api/Canny");
				errorMessage.setText("Canny Edit Applied");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}
			
		
		});
		
		orb.setOnAction(event->{
			if(imageAvailable)
			{
				serverRequest("/api/ORB");
				errorMessage.setText("ORB Edit Applied");
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}
			

		});
		removeEdits.setOnAction(event->{
			if(imageAvailable)
			{
				ShowImage(image);
				errorMessage.setText("Edits Removed");
				
			}
			else
			{
				errorMessage.setText("Please Choose Image first");
			}
			
		});
		
		//detect faces in the picture given
		faces.setOnAction(event->{
			if(imageAvailable)
			{
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				
				Mat src = Imgcodecs.imread("data/"+file.getName());
				
				
				//xml file helps program figure out what a face looks like
				String xmlFile = "xml/lbpcascade_frontalface.xml";
				//String xmlFile = "xml/cars.xml";
				CascadeClassifier casClass = new CascadeClassifier(xmlFile);
			
				MatOfRect faceDetection = new MatOfRect();
				
				casClass.detectMultiScale(src, faceDetection);
				
				//place rectangle on a face that has been detected
				for(Rect rect: faceDetection.toArray()) {
					Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height) , new Scalar(0, 0, 255), 3);
				}		
				
				//creating an edited image version
				String filename ="data/FaceDetected/"+file.getName();
				Imgcodecs.imwrite(filename, src);
				
				try {
					Image image = new Image(new FileInputStream(new File(filename)));
					//Image image = new Image(new FileInputStream(new File("data/FaceDetected/"+file.getName())));
					
					ShowImage(image);
					//imageDisplay.setImage(image);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				errorMessage.setText(String.format("Detected faces: %d", faceDetection.toArray().length));
			}else
			{
				errorMessage.setText("Choose an image first");
			}
		});
		
		
		//error message text
		errorMessage.resize(50, 80);
		
		//adding button nodes to buttons
		buttons.setSpacing(10);
		buttons.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID,new CornerRadii(5), BorderWidths.DEFAULT)));
		buttons.setMinSize(300, 300);
		
		buttons.getChildren().add(chooseFile);
		
		//rotate and gray scale buttons
		HBox rotateAndGrayScale = new HBox();
		rotateAndGrayScale.setSpacing(10);
		rotateAndGrayScale.getChildren().addAll(rotate,grayScale);
		
		//erosion and dilation buttons
		HBox erosionAndDilation = new HBox();
		erosionAndDilation.setSpacing(10);
		erosionAndDilation.getChildren().addAll(erosion,dilation);
		
		//canny and crop
		HBox cannyAndCrop = new HBox();
		cannyAndCrop.setSpacing(10);
		cannyAndCrop.getChildren().addAll(canny,crop);
		
		//fast features
		HBox fastAndFastFeatures = new HBox();
		fastAndFastFeatures.setSpacing(10);
		fastAndFastFeatures.getChildren().addAll(removeEdits,fastImage);
		
		//canny features and faces
		HBox cannyAndFaces = new HBox();
		cannyAndFaces.setSpacing(10);
		cannyAndFaces.getChildren().addAll(orb,faces);
		
		HBox additional = new HBox();
		additional.setSpacing(10);
		additional.getChildren().addAll(stylization,detailEnhance);
		
		//display styling
		display.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID,new CornerRadii(5), BorderWidths.DEFAULT)));
		this.setPadding(new Insets(12));
		this.setSpacing(12);
		
		buttons.getChildren().addAll(rotateAndGrayScale,erosionAndDilation,cannyAndCrop,fastAndFastFeatures,cannyAndFaces,additional,edgePreserving,errorMessage);
		
		this.getChildren().addAll(buttons,display);
	}
	
	/**
	 * this function initializes all streams to be used 
	 */
	private void initStreams()
	{
		try {
			socket = new Socket("localhost",5000);
			is = socket.getInputStream();
			os = socket.getOutputStream();
			br = new BufferedReader(new InputStreamReader(is));
					
			bos = new BufferedOutputStream(os); 
			dos = new DataOutputStream(bos);
			dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * this function closes all open streams
	 */
	private void closeStreams()
	{
		try
		{
			if(socket!=null)
			{
				socket.close();
			}
			if(dos!=null)
			{
				dos.close();
			}
			if(dis!=null)
			{
				dis.close();
			}
			if(is !=null)
			{
				is.close();
			}
			if(os != null)
			{
				os.close();
			}
			if(bos !=null)
			{
				bos.close();
			}
			if(br!=null)
			{
				br.close();
			}	
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param URL the request we want to make to the server
	 */
	private void serverRequest(String URL)
	{	
		//initialize streams before we use them
		initStreams();
		String encodedFile = null;
		try
		{
			//get image from the chosen image View
			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fis.read(bytes);
			
			encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
			byte[] bytesToSend = encodedFile.getBytes();
			
			//construct a POST HTTP REQUEST
			dos.write(("POST " + URL + " HTTP/1.1\r\n").getBytes());
			dos.write(("Content-Type: " + "application/text\r\n").getBytes());
			dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());
			dos.write(("\r\n").getBytes());
			dos.write(bytesToSend);
			dos.flush();
			dos.write(("\r\n").getBytes());
			dos.flush();
			 
			//get response from the server
			String line = "";
			
			String imageData = "";
			while((line =br.readLine()) !=null)
			{
				imageData +=line;
			}
			
			//string trimming to get image value
			String base64Str = imageData.substring(imageData.indexOf('\'') + 1, imageData.lastIndexOf('}') - 1);
			byte[] decodedString = Base64.getDecoder().decode(base64Str);
			
			//create image from server then output it to the display
			Image image = new Image(new ByteArrayInputStream(decodedString));
			ShowImage(image);
			fis.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	 
		//finally close streams 
		closeStreams();
	}
	
	/**
	 * 
	 * @param image the image to be displayed on image view
	 */
	private void ShowImage(Image image)
	{
		imageDisplay = null;
		imageDisplay = new ImageView();
					
		imageDisplay.setImage(image);
		
		//fit image to imageview if its larger than reqired image
		if(image.getHeight()>550 || image.getWidth()>655)
		{
			imageDisplay.setFitHeight(550);
			imageDisplay.setFitWidth(655);
		}
		
		if(display.getChildren().isEmpty())
		{
			display.getChildren().add(imageDisplay);
		}
		else
		{
			display.getChildren().remove(0);
			display.getChildren().add(imageDisplay);
		}
	}
	
}

