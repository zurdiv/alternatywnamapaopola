//
//  PlaceCell.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 24.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "PlaceCell.h"
#import "AMODataManager.h"

#define FONT_SIZE 17
#define MAX_TEXT_WIDTH 252
#define CELL_HEIGHT_EXCLUDING_DYNAMICS 44 - 21


@implementation PlaceCell

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
    self.backgroundColor = [AMODataManager shared].colorListBackground;
    
    UIView *view = [[UIView alloc] initWithFrame:CGRectZero];
    view.backgroundColor = [AMODataManager shared].colorListSelected;
    self.selectedBackgroundView = view;
    [view release];
}

- (void)dealloc
{
    [_uiText release];
    [_uiNumber release];
    [super dealloc];
}

+ (int)cellHeightForData:(NSDictionary *)data
{
    NSString *title = [data objectForKey:kName];
    int titleHeight = 0;
    
    if (title && title.length > 0) {
        UIFont *font = [UIFont fontWithName:FONT_LIBEL size:FONT_SIZE];

        titleHeight = [title sizeWithFont:font
                        constrainedToSize:CGSizeMake(MAX_TEXT_WIDTH, 500)
                            lineBreakMode:UILineBreakModeWordWrap].height;
    }
    
    return CELL_HEIGHT_EXCLUDING_DYNAMICS + titleHeight;
}

- (void)fillWithData:(NSDictionary *)data row:(int)row
{
    _uiText.text = [data objectForKey:kName];
    _uiText.font = [UIFont fontWithName:FONT_LIBEL size:FONT_SIZE];
    
    int index = [[AMODataManager shared].places indexOfObject:data];
    
    _uiNumber.font = _uiText.font;
    _uiNumber.text = [NSString stringWithFormat:@"%d", index + 1];
    
    CGRect frame = _uiText.frame;
    frame.size = [_uiText sizeThatFits:CGSizeMake(MAX_TEXT_WIDTH, 500)];
    _uiText.frame = frame;
}

@end
