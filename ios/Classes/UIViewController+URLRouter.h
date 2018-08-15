//
//  UIViewController+URLRouter.h
//  hybrid_stack_manager
//
//  Created by KyleWong on 2018/8/13.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIViewController (URLRouter)
- (instancetype)initWithURL:(NSURL *)url query:(NSDictionary *)query nativeParams:(NSDictionary *)nativeParams;
@end

NS_ASSUME_NONNULL_END
