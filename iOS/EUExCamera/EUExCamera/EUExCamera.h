//
//  EUExCamera.h
//  testjs1
//
//  Created by zywx on 11-8-26.
//  Copyright 2011 __MyCompanyName__. All rights reserved.

#import "EUExBase.h"

@interface EUExCamera : EUExBase  <UIImagePickerControllerDelegate,UINavigationControllerDelegate>{
    BOOL isCompress;//是否压缩
    float scale;//缩放比例
}

-(void)uexSuccessWithOpId:(int)inOpId dataType:(int)inDataType data:(NSString *)inData;
@end
