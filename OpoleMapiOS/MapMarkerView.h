//
//  MapMarkerView.h
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 24.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <UIKit/UIKit.h>

#define MARKER_WIDTH 40
#define MARKER_HEIGHT 50


@protocol MapMarkerDelegate <NSObject>
- (void)mapMarkerWantToShowInfoViewOfPlace:(NSDictionary *)place;
@end


@interface MapMarkerView : UIView
{
    
    NSDictionary *_place;
    IBOutlet UIImageView *_uiMarker;
    IBOutlet UILabel *_uiNumber;
}

@property (nonatomic, assign) id <MapMarkerDelegate> delegate;


- (id)initWithPlace:(NSDictionary *)place category:(int)categoryId atPoint:(CGPoint)point isSelected:(Boolean)selected;

@end
