#import "LanguageManager.h"


@implementation LanguageManager

+ (BOOL)isSupportedLanguage:(NSString *)language
{
    if ([language isEqualToString:kLMEnglish]) {
        return YES;
    } else if ([language isEqualToString:kLMPolish]) {
        return YES;
    } else if ([language isEqualToString:kLMDeutsch]) {
        return YES;
    }
    
    return NO;
}

+ (NSString *)localizedString:(NSString *)key
{
    NSString *selectedLanguage = [LanguageManager selectedLanguage];
	NSString *path = [[NSBundle mainBundle] pathForResource:selectedLanguage ofType:@"lproj"];
    
	NSBundle* languageBundle = [NSBundle bundleWithPath:path];
	NSString* str = [languageBundle localizedStringForKey:key value:@"" table:nil];    
    
	return str;
}

+ (void)setSelectedLanguage:(NSString *)language
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    if ([self isSupportedLanguage:language]) {
        [userDefaults setObject:language forKey:kLMSelectedLanguageKey];
    } else {
        // not supported
        [userDefaults setObject:nil forKey:kLMSelectedLanguageKey];
    }
    
    [userDefaults synchronize];
}

+ (NSString *)systemLanguage
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *systemLanguage = [[userDefaults objectForKey:@"AppleLanguages"] objectAtIndex:0];
    
    return systemLanguage;
}

+ (NSString *)selectedLanguage
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *selectedLanguage = [userDefaults stringForKey:kLMSelectedLanguageKey];
    
    if (selectedLanguage == nil) {
        
        NSString *systemLanguage = [LanguageManager systemLanguage];
        
        // if system language is supported by LanguageManager, set it as selected language
        if ([self isSupportedLanguage:systemLanguage]) {
            [self setSelectedLanguage:systemLanguage];
        } else {
            // set the LanguageManager default language as selected language
            [self setSelectedLanguage:kLMDefaultLanguage];
        }
    }
    
    return [userDefaults stringForKey:kLMSelectedLanguageKey];
}

@end
