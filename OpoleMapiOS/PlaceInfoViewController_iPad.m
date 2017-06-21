//
//  PlaceInfoViewController_iPad.m
//  AlternatywnaMapaOpola
//
//  Created by mac on 24.07.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "PlaceInfoViewController_iPad.h"
#import "AMODataManager.h"
#import "AppDelegate.h"

#define FONT_SIZE 48
#define MAX_NAME_WIDTH 650
#define DIFFERENCE_BETWEEN_ORIENTATION 35;

@interface PlaceInfoViewController_iPad ()

@end

@implementation PlaceInfoViewController_iPad

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
    
    CGRect frame;
    
    // marker
    _uiMarker.image = [UIImage imageNamed:[NSString stringWithFormat:@"marker%d", _categoryId]];
    
    //Numer:
    _uiNumber.font = [UIFont fontWithName:FONT_LIBEL size:22];
    _uiNumber.text = [NSString stringWithFormat:@"%@", [_place objectForKey:kLp]];
    frame = _uiNumber.frame;
    frame.origin.x = _uiMarker.frame.origin.x + 5;
    _uiNumber.frame = frame;
    
    //Nazwa
    _uiName.font = [UIFont fontWithName:FONT_LIBEL size:FONT_SIZE];
    _uiName.text = [_place objectForKey:kName];
    frame = _uiName.frame;
        
    frame.size = [_uiName.text sizeWithFont:_uiName.font constrainedToSize:CGSizeMake(MAX_NAME_WIDTH, 500) lineBreakMode:UILineBreakModeWordWrap];
    _uiName.frame = frame;
    
    //Opis
    _uiDescription.font = [UIFont fontWithName:FONT_LIBEL size:42];
    _uiDescription.backgroundColor = [AMODataManager shared].colorListBackground;
    _uiDescription.text = [_place objectForKey:kDescription];  
    frame = _uiDescription.frame;
    frame.origin.x = _uiName.frame.origin.x;
    frame.origin.y = _uiName.frame.origin.y + _uiName.frame.size.height + 10;
    frame.size.width = MAX_NAME_WIDTH;
    frame.size.height = 425;
    _uiDescription.frame = frame;
    
    // cechy
    _features = [[[_place objectForKey:kFeature] componentsSeparatedByString:@" "] retain];
    int x = _uiMarker.frame.origin.x + _uiMarker.frame.size.width + 50;
    int y = _uiMarker.frame.origin.y + 10;
    int index = 0;
        
    for (NSString *feat in _features) {
        UIImage *image = [UIImage imageNamed:feat];
        UIButton *button = [[UIButton alloc] initWithFrame:CGRectZero];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        button.frame = CGRectMake(x, y, 32, 32);
        [button addTarget:self action:@selector(clickIcon:) forControlEvents:UIControlEventTouchUpInside];
        button.tag = index++;
            
        x += 36;
        [self.view addSubview:button];
        [button release];
    }

    //Pozycja elementów jeżeli ekran jest pionowo:
    if([[UIApplication sharedApplication] statusBarOrientation]==UIInterfaceOrientationPortrait ||[[UIApplication sharedApplication] statusBarOrientation]==UIInterfaceOrientationPortraitUpsideDown)
    {
        frame = _uiMarker.frame;
        frame.origin.x += DIFFERENCE_BETWEEN_ORIENTATION;
        _uiMarker.frame = frame;
        frame = _uiNumber.frame;
        frame.origin.x = _uiMarker.frame.origin.x + 5;
        _uiNumber.frame = frame;
        frame = _uiName.frame;
        frame.origin.x += DIFFERENCE_BETWEEN_ORIENTATION;
        _uiName.frame = frame;
        frame = _uiDescription.frame;
        frame.origin.x = _uiName.frame.origin.x;
        frame.size.height = 600;
        _uiDescription.frame = frame;
        [_uiButton setFrame:CGRectMake((MAX_NAME_WIDTH - 75), 800, 75, 50)];
    }
    NSString *title = NSLocalizedString(@"BackButton", @"BackButton");
    UIBarButtonItem *button = [[UIBarButtonItem alloc] initWithTitle:title style:UIBarButtonItemStylePlain target:self action:@selector(backButtonPressed:)];
    [self.navigationItem setRightBarButtonItem:button animated:NO];
    [button release];
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

- (IBAction)backButtonPressed:(id)sender {
    MapViewController *mvc = [[MapViewController alloc] initWithPlace:_place category:_categoryId];
    if([AppDelegate getIsPad])
    {
        [mvc.navigationItem setLeftBarButtonItem:self.navigationItem.leftBarButtonItem];
        UISplitViewController *svc = [AppDelegate getSVC];
        //NSMutableArray *arr = [[NSMutableArray alloc] initWithArray:svc.viewControllers];
        //[arr replaceObjectAtIndex:1 withObject:mvc];
        //svc.viewControllers = arr;
        //[arr release];
        
        NSMutableArray *maps = [[NSMutableArray alloc] initWithArray:[[svc.viewControllers objectAtIndex:1] viewControllers]];
        
        [maps removeLastObject];
        [maps addObject:mvc];
        svc.delegate = mvc;
        [[svc.viewControllers objectAtIndex:1] setViewControllers:maps];
        [maps release];

    }
    else
    {
        [self.navigationController pushViewController:mvc animated:YES];
        [mvc release];
    }
}

@synthesize popover;
- (void)splitViewController:(UISplitViewController *)svc willHideViewController:(UIViewController *)aViewController withBarButtonItem:(UIBarButtonItem *)barButtonItem forPopoverController:(UIPopoverController *)pc
{
    // popover = pc;
    barButtonItem.title = @"Menu";
    //[self.navigationController setNavigationBarHidden:NO animated:YES];
    [self.navigationItem setLeftBarButtonItem:barButtonItem animated:NO];
}
- (void)splitViewController:(UISplitViewController *)svc willShowViewController:(UIViewController *)aViewController invalidatingBarButtonItem:(UIBarButtonItem *)barButtonItem
{
    [self.navigationItem setLeftBarButtonItem:nil animated:NO];
    // [self.navigationController setNavigationBarHidden:YES animated:YES];
    // [popover release];
}

- (void) willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    int descriptionHeightExtend = 175;
    if(toInterfaceOrientation == UIInterfaceOrientationPortrait || toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown)
    {
        CGRect frame;
        
        frame = _uiMarker.frame;
        frame.origin.x += DIFFERENCE_BETWEEN_ORIENTATION;
        _uiMarker.frame = frame;
        
        frame = _uiNumber.frame;
        frame.origin.x = _uiMarker.frame.origin.x + 5;
        _uiNumber.frame = frame;
        
        frame = _uiName.frame;
        frame.origin.x += DIFFERENCE_BETWEEN_ORIENTATION;
        _uiName.frame = frame;
        
        frame = _uiDescription.frame;
        frame.origin.x = _uiName.frame.origin.x;
        frame.size.height += descriptionHeightExtend;
        _uiDescription.frame = frame;
        
        [_uiButton setFrame:CGRectMake((MAX_NAME_WIDTH - 75), 800, 75, 50)];
        
    }
    else
    {
        CGRect frame;
        
        frame = _uiMarker.frame;
        frame.origin.x -= DIFFERENCE_BETWEEN_ORIENTATION;
        _uiMarker.frame = frame;
        
        frame = _uiNumber.frame;
        frame.origin.x = _uiMarker.frame.origin.x + 5;
        _uiNumber.frame = frame;
        
        frame = _uiName.frame;
        frame.origin.x -= DIFFERENCE_BETWEEN_ORIENTATION;
        _uiName.frame = frame;
        
        frame = _uiDescription.frame;
        frame.origin.x = _uiName.frame.origin.x;
        frame.size.height -= descriptionHeightExtend;
        _uiDescription.frame = frame;
        
        [_uiButton setFrame:CGRectMake((MAX_NAME_WIDTH - 75), 625, 75, 50)];
    }
}

@end
