//
//  PlaceInfoViewController_iPad.h
//  AlternatywnaMapaOpola
//
//  Created by mac on 24.07.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UITextView+DisableCopyPaste.h"
#import "MapViewController.h"

@interface PlaceInfoViewController_iPad : UIViewController <UISplitViewControllerDelegate>
{
    NSDictionary *_place;
    int _categoryId;
    NSArray *_features;
    
    IBOutlet UILabel *_uiNumber;
    IBOutlet UILabel *_uiName;
    IBOutlet UITextView *_uiDescription;
    IBOutlet UIImageView *_uiMarker;
    UIButton *_uiButton;
};
@property (nonatomic, retain) IBOutlet UINavigationItem *navBarItem;
@property (nonatomic, strong) UIPopoverController *popover;
- (id)initWithPlace:(NSDictionary *)place category:(int)categoryId;

@end
