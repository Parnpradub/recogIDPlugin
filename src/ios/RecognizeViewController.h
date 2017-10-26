//  RecognizeViewController.h
//  IDRecog

#import <UIKit/UIKit.h>
#import <TesseractOCR/TesseractOCR.h>
#import <AVFoundation/AVCaptureSession.h>
#import <AVFoundation/AVCaptureStillImageOutput.h>
#import <AVFoundation/AVCaptureInput.h>
#import <AVFoundation/AVCaptureVideoPreviewLayer.h>
#import <AVFoundation/AVCaptureVideoDataOutput.h>
#import "DocRecognize.h"

@interface RecognizeViewController : UIViewController<G8TesseractDelegate>

@property (readwrite) DocRecognize *main;

@end

