#import <Foundation/Foundation.h>

// supported languages
#define	kLMEnglish  @"en"
#define kLMPolish   @"pl"
#define kLMDeutsch   @"de"

#define kLMDefaultLanguage  kLMEnglish
#define kLMSelectedLanguageKey  @"kSelectedLanguageKey"


@interface LanguageManager : NSObject

+ (BOOL)isSupportedLanguage:(NSString *)language;
+ (NSString *)localizedString:(NSString *)key;
+ (void)setSelectedLanguage:(NSString *)language;
+ (NSString *)selectedLanguage;
+ (NSString *)systemLanguage;

@end
