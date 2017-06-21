//
//  UITextVew+DisableCopyPaste.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 24.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "UITextView+DisableCopyPaste.h"


@implementation UITextView (DisableCopyPaste)

- (BOOL)canBecomeFirstResponder
{
    return NO;
}

@end
