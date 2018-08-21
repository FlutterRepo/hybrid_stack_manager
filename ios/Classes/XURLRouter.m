//
//  XURLRouter.m
//  Runner
//
//  Created by KyleWong on 2018/8/13.
//  Copyright © 2018 The Chromium Authors. All rights reserved.
//

#import "XURLRouter.h"
#import "XFlutterModule.h"

@implementation XURLRouter
+ (instancetype)sharedInstance{
    static XURLRouter *sInstance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sInstance = [XURLRouter new];
    });
    return sInstance;
}
@end

void XOpenURLWithQueryAndParams(NSString *url,NSDictionary *query,NSDictionary *params){
    NSURL *tmpUrl = [NSURL URLWithString:url];
    UINavigationController *rootNav = (UINavigationController*)[UIApplication sharedApplication].delegate.window.rootViewController;
    if(![kOpenUrlPrefix isEqualToString:tmpUrl.scheme])
        return;
    if([[query objectForKey:@"flutter"] boolValue]){
        [[XFlutterModule sharedInstance] openURL:url query:query params:params];
        return;
    }
    NativeOpenUrlHandler handler = [XURLRouter sharedInstance].nativeOpenUrlHandler;
    if(handler!=nil)
    {
        UIViewController *vc = handler(url,query,params);
        if(vc!=nil)
            [rootNav pushViewController:vc animated:YES];
    }
}
