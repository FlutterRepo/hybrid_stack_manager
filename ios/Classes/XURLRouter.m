//
//  XURLRouter.m
//  Runner
//
//  Created by KyleWong on 2018/8/13.
//  Copyright © 2018 The Chromium Authors. All rights reserved.
//

#import "XURLRouter.h"
#import "XFlutterModule.h"

NativeOpenUrlHandler sNativeOpenUrlHandler = nil;

void XOpenURLWithQueryAndParams(NSString *url,NSDictionary *query,NSDictionary *params){
    NSURL *tmpUrl = [NSURL URLWithString:url];
    UINavigationController *rootNav = (UINavigationController*)[UIApplication sharedApplication].delegate.window.rootViewController;
    if(![kOpenUrlPrefix isEqualToString:tmpUrl.scheme])
        return;
    if([[query objectForKey:@"flutter"] boolValue]){
        [[XFlutterModule sharedInstance] openURL:url query:query params:params];
        return;
    }
    if(sNativeOpenUrlHandler!=nil)
    {
        UIViewController *vc = sNativeOpenUrlHandler(url,query,params);
        if(vc!=nil)
            [rootNav pushViewController:vc animated:YES];
    }
}
