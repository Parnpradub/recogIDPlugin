//  RecognizeViewController.m
//  IDRecog

#import "RecognizeViewController.h"


@interface RecognizeViewController ()

@end

@implementation RecognizeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor blackColor];
    
    AVCaptureSession *session;
    
    session = [[AVCaptureSession alloc] init];
    [session setSessionPreset:AVCaptureSessionPresetPhoto];
    
    AVCaptureDevice *inputDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    NSError *error;
    AVCaptureDeviceInput *deviceInput = [AVCaptureDeviceInput deviceInputWithDevice:inputDevice error:&error];
    
    if ([session canAddInput:deviceInput]) {
        [session addInput:deviceInput];
    }
    
    AVCaptureVideoPreviewLayer *previewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:session];
    [previewLayer setVideoGravity:AVLayerVideoGravityResizeAspect];
    
    CALayer *rootLayer = [[self view] layer];
    [rootLayer setMasksToBounds:YES];
    CGRect frame = CGRectMake(0, 0, rootLayer.bounds.size.width, rootLayer.bounds.size.height);
    
//    CGRect frame = rootLayer.bounds;
    [previewLayer setFrame:frame];
    [rootLayer insertSublayer:previewLayer atIndex:0];

    float ltx = rootLayer.bounds.size.width/10;
    float lty = rootLayer.bounds.size.height/11*5;
    float lbx = ltx;
    float lby = lty/5*6;
    float rtx = ltx*9;
    float rty = lty;
    float rbx = rtx;
    float rby = lby;
    
    [self makeLineLayer:rootLayer lineFromPointA:CGPointMake(ltx, lty) toPointB:CGPointMake(rtx, rty)];
    [self makeLineLayer:rootLayer lineFromPointA:CGPointMake(ltx, lty) toPointB:CGPointMake(lbx, lby)];
    [self makeLineLayer:rootLayer lineFromPointA:CGPointMake(lbx, lby) toPointB:CGPointMake(rbx, rby)];
    [self makeLineLayer:rootLayer lineFromPointA:CGPointMake(rtx, rty) toPointB:CGPointMake(rbx, rby)];
    
//    stillImageOutput = [[AVCaptureStillImageOutput alloc] init];
//    NSDictionary *outputSettings = [[NSDictionary alloc] initWithObjectsAndKeys:AVVideoCodecJPEG, AVVideoCodecKey, nil];
//    [stillImageOutput setOutputSettings:outputSettings];
//    [session addOutput:stillImageOutput];

    AVCaptureVideoDataOutput *output = [[AVCaptureVideoDataOutput alloc] init];
    [session addOutput:output];
    
    // Configure your output.
    dispatch_queue_t queue = dispatch_queue_create("myQueue", NULL);
    [output setSampleBufferDelegate:self queue:queue];
    
    // Specify the pixel format
    output.videoSettings =
    [NSDictionary dictionaryWithObject:
     [NSNumber numberWithInt:kCVPixelFormatType_32BGRA]
                                forKey:(id)kCVPixelBufferPixelFormatTypeKey];

//    textview = [[UITextView alloc] initWithFrame:CGRectMake(100, 400, 200, 30)];
//    textview.text = @"Id number";
//
//    [[self view] addSubview:textview];
//
//    imageview = [[UIImageView alloc] initWithFrame:CGRectMake(0, 500, 300, 50)];
//    imageview.image = [UIImage imageNamed:@"demotest.png"];
//
//    [[self view] addSubview:imageview];
    
    
    [session startRunning];
}

-(UIInterfaceOrientationMask)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

-(NSUInteger)navigationControllerSupportedInterfaceOrientations:(UINavigationController *)navigationController {
    return UIInterfaceOrientationMaskPortrait;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer fromConnection:(AVCaptureConnection *)connection
{
    // Create a UIImage from the sample buffer data
    [connection setVideoOrientation:AVCaptureVideoOrientationPortrait];
    UIImage *image = [self imageFromSampleBuffer:sampleBuffer];
    
    float w = image.size.width;
    float h = image.size.height;
    
    CGRect rect = CGRectMake(w/10,h/11*5,w/10*8,h/11);
    
    // Create bitmap image from original image data,
    // using rectangle to specify desired crop area
    CGImageRef imageRef = CGImageCreateWithImageInRect([image CGImage], rect);
    
    UIImage *img = [UIImage imageWithCGImage:imageRef];
    CGImageRelease(imageRef);
    
    G8Tesseract *tesseract = [[G8Tesseract alloc] initWithLanguage:@"eng"];
    tesseract.delegate = self;
    tesseract.image = [img g8_blackAndWhite];
    [tesseract recognize];
    
    [self getLineStr:[tesseract recognizedText]];
}

-(void)makeLineLayer:(CALayer *)layer lineFromPointA:(CGPoint)pointA toPointB:(CGPoint)pointB
{
    CAShapeLayer *line = [CAShapeLayer layer];
    UIBezierPath *linePath=[UIBezierPath bezierPath];
    [linePath moveToPoint: pointA];
    [linePath addLineToPoint:pointB];
    line.path=linePath.CGPath;
    line.fillColor = nil;
    line.lineWidth = 2.0;
    line.opacity = 1.0;
    line.strokeColor = [UIColor greenColor].CGColor;
    [[self.view layer] addSublayer:line];
}

- (UIImage *) imageFromSampleBuffer:(CMSampleBufferRef) sampleBuffer
{
    // Get a CMSampleBuffer's Core Video image buffer for the media data
    CVImageBufferRef imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    // Lock the base address of the pixel buffer
    CVPixelBufferLockBaseAddress(imageBuffer, 0);
    
    // Get the number of bytes per row for the pixel buffer
    void *baseAddress = CVPixelBufferGetBaseAddress(imageBuffer);
    
    // Get the number of bytes per row for the pixel buffer
    size_t bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer);
    // Get the pixel buffer width and height
    size_t width = CVPixelBufferGetWidth(imageBuffer);
    size_t height = CVPixelBufferGetHeight(imageBuffer);
    
    // Create a device-dependent RGB color space
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    
    // Create a bitmap graphics context with the sample buffer data
    CGContextRef context = CGBitmapContextCreate(baseAddress, width, height, 8,
                                                 bytesPerRow, colorSpace, kCGBitmapByteOrder32Little | kCGImageAlphaPremultipliedFirst);
    // Create a Quartz image from the pixel data in the bitmap graphics context
    CGImageRef quartzImage = CGBitmapContextCreateImage(context);
    // Unlock the pixel buffer
    CVPixelBufferUnlockBaseAddress(imageBuffer,0);
    
    // Free up the context and color space
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    // Create an image object from the Quartz image
    UIImage *image = [UIImage imageWithCGImage:quartzImage];
    
    // Release the Quartz image
    CGImageRelease(quartzImage);
    
    return (image);
}

-(void)getLineStr:(NSString *)input{
    NSArray* lines = [[input stringByReplacingOccurrencesOfString:@" " withString:@""] componentsSeparatedByString: @"\n"];
    
    for(int i = 0; i < lines.count ; i++){
        NSArray* pieces = [[lines objectAtIndex: i] componentsSeparatedByString: @"-"];
        
        if( pieces.count == 3 ){
            NSString *temp1 = [[[pieces objectAtIndex:0] componentsSeparatedByCharactersInSet:
                    [[NSCharacterSet decimalDigitCharacterSet] invertedSet]]
                   componentsJoinedByString:@""];
            NSString *temp2 = [[[pieces objectAtIndex:1] componentsSeparatedByCharactersInSet:
                                [[NSCharacterSet decimalDigitCharacterSet] invertedSet]]
                               componentsJoinedByString:@""];
            NSString *temp3 = [[[pieces objectAtIndex:2] componentsSeparatedByCharactersInSet:
                                [[NSCharacterSet decimalDigitCharacterSet] invertedSet]]
                               componentsJoinedByString:@""];
            if([temp1 length] == 8 && [temp2 length] == 1 && [temp3 length] == 2 ){
                NSString *result = [NSString stringWithFormat:@"%@-%@-%@", temp1, temp2, temp3];
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.main completeWith:result];
                    [self dismissViewControllerAnimated:NO completion:nil];
                });
            }
        }
    }
}


@end
