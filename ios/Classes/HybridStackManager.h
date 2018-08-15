#import <Flutter/Flutter.h>

@interface HybridStackManager : NSObject<FlutterPlugin>
+ (instancetype)sharedInstance;
@property (nonatomic,strong) FlutterMethodChannel* methodChannel;
@property (nonatomic,strong) NSDictionary* mainEntryParams;
@end

