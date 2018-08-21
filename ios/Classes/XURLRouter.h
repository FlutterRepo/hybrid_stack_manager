//
//  XURLRouter.h
//  Runner
//
//  Created by KyleWong on 2018/8/13.
//  Copyright © 2018 The Chromium Authors. All rights reserved.
//

#import <Foundation/Foundation.h>
#define kOpenUrlPrefix  @"hrd"

typedef UIViewController* (^NativeOpenUrlHandler)(NSString *,NSDictionary *,NSDictionary *);
void XOpenURLWithQueryAndParams(NSString *url,NSDictionary *query,NSDictionary *params);

@interface XURLRouter : NSObject
@property (nonatomic,weak) NativeOpenUrlHandler nativeOpenUrlHandler;
+ (instancetype)sharedInstance;
@end
