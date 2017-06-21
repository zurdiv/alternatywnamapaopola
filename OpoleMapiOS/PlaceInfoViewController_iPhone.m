//
//  PLaceInfoViewController_iPhone.m
//  AlternatywnaMapaOpola
//
//  Created by mac on 24.07.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "PlaceInfoViewController_iPhone.h"
#import "AMODataManager.h"
#import "AppDelegate.h"

#define FONT_SIZE 22
#define MAX_NAME_WIDTH 231

@interface PlaceInfoViewController_iPhone ()

@end

@implementation PlaceInfoViewController_iPhone

- (id)initWithPlace:(NSDictionary *)place category:(int)categoryId
{
    self = [super initWithNibName:NSStringFromClass([self class]) bundle:nil];
    if (self) {
        _place = place;
        _categoryId = categoryId;
    }
    
    return self;
}

- (void)dealloc
{
    [_features release];
    [_uiNumber release];
    [_uiName release];
    [_uiDescription release];
    [_uiMarker release];
    [super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.view.backgroundColor = [AMODataManager shared].colorListBackground;
    self.title = NSLocalizedString(@"PlaceDesc", nil);
    
    _uiNumber.font = [UIFont fontWithName:FONT_LIBEL size:FONT_SIZE];
    _uiNumber.text = [NSString stringWithFormat:@"%@", [_place objectForKey:kLp]];
    _uiName.font = [UIFont fontWithName:FONT_LIBEL size:FONT_SIZE];
    _uiName.text = [_place objectForKey:kName];
    
    CGRect frame = _uiName.frame;
    frame.size = [_uiName sizeThatFits:CGSizeMake(MAX_NAME_WIDTH, 500)];
    _uiName.frame = frame;
    
    _uiDescription.font = [UIFont fontWithName:FONT_LIBEL size:17];
    _uiDescription.backgroundColor = [AMODataManager shared].colorListBackground;
    _uiDescription.text = [_place objectForKey:kDescription];
    
    frame = _uiDescription.frame;
    frame.origin.y = _uiName.frame.origin.y + _uiName.frame.size.height + 10;
    //frame.size.height = self.view.frame.size.height - frame.origin.y - 44 - 20;
    _uiDescription.frame = frame;
    
    
    // cechy
    _features = [[[_place objectForKey:kFeature] componentsSeparatedByString:@" "] retain];
    int x = self.view.frame.size.width - 8 - 36 * _features.count;
    int index = 0;
    
    for (NSString *feat in _features) {
        
        UIImage *image = [UIImage imageNamed:feat];
        UIButton *button = [[UIButton alloc] initWithFrame:CGRectZero];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        button.frame = CGRectMake(x, 10, 32, 32);
        [button addTarget:self action:@selector(clickIcon:) forControlEvents:UIControlEventTouchUpInside];
        button.tag = index++;
        
        x += 36;
        [self.view addSubview:button];
        [button release];
    }
    
    // marker
    _uiMarker.image = [UIImage imageNamed:[NSString stringWithFormat:@"marker%d", _categoryId]];
}

- (void)viewDidUnload
{
    [_uiNumber release];
    _uiNumber = nil;
    [_uiName release];
    _uiName = nil;
    [_uiDescription release];
    _uiDescription = nil;
    [_uiMarker release];
    _uiMarker = nil;
    [super viewDidUnload];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)clickIcon:(UIButton *)sender
{
    NSString *desc = NSLocalizedString([_features objectAtIndex:sender.tag], nil);
    [AMODataManager showAlertWithTitle:@"" message:desc delegate:nil];
}

@end
