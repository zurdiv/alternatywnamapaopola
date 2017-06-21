
#import "MapTilesView.h"
#import "MapTilesLayer.h"


@implementation MapTilesView

+ (Class)layerClass
{
    return [MapTilesLayer class];
}

- (void)drawRect:(CGRect)rect
{
    int firstCol = floorf(CGRectGetMinX(rect) / _tileSize);
    int lastCol = floorf((CGRectGetMaxX(rect) - 1) / _tileSize);
    int firstRow = floorf(CGRectGetMinY(rect) / _tileSize);
    int lastRow = floorf((CGRectGetMaxY(rect) - 1) / _tileSize);
    
    NSString *name, *path;
    UIImage *tile;
    
    for (int row = firstRow; row <= lastRow; row++) {
        for (int col = firstCol; col <= lastCol; col++) {
            
            name = [NSString stringWithFormat:@"map%d_%d_%d", _zoomLevel, col, row];
            path = [[NSBundle mainBundle] pathForResource:name ofType:@"png"];
            tile = [[UIImage alloc] initWithContentsOfFile:path];
            
            if (tile) {
                CGRect tileRect = CGRectMake(_tileSize * col, _tileSize * row, _tileSize, _tileSize);
                tileRect = CGRectIntersection(self.bounds, tileRect);
                
                [tile drawInRect:tileRect];
                [tile release];
            }
        }
    }
}

@end
