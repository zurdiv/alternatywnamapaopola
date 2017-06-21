//
//  MapMarkerView.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 24.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "MapMarkerView.h"
#import "AMODataManager.h"


@implementation MapMarkerView
- (id)initWithPlace:(NSDictionary *)place category:(int)categoryId atPoint:(CGPoint)point isSelected:(Boolean)selected
{
    self = [super init];
    if (self) {
        _place = place;
        
        UIView *view = [[[NSBundle mainBundle] loadNibNamed:NSStringFromClass([self class]) owner:self options:nil] objectAtIndex:0];
        [self addSubview:view];
        
        _uiNumber.text = [NSString stringWithFormat:@"%@", [place objectForKey:kLp]];
        _uiNumber.font = [UIFont fontWithName:FONT_LIBEL size:17];
        
        if(selected) {
            _uiMarker.image = [UIImage imageNamed:[NSString stringWithFormat:@"marker%dsel", categoryId]];
        } else {
            _uiMarker.image = [UIImage imageNamed:[NSString stringWithFormat:@"marker%d", categoryId]];
        }
        self.frame = CGRectOffset(view.frame, point.x - _uiMarker.frame.size.width / 2, point.y - _uiMarker.frame.size.height);
    }
    
    return self;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [_delegate mapMarkerWantToShowInfoViewOfPlace:_place];
}

- (void)dealloc {
    [_uiNumber release];
    [_uiMarker release];
    [super dealloc];
}
@end
