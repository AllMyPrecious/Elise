package site.zido.elise.selector;

/**
 * Create selector tool
 *
 * @author zido
 */
public class Selectors {
    public static CssSelector css(String selector) {
        return new CssSelector(selector);
    }

    public static CssSelector css(String selector, String attrName) {
        return new CssSelector(selector, attrName);
    }

    public static XpathSelector xpath(String xpath) {
        return new XpathSelector(xpath);
    }

    public static RegexSelector regex(String regex) {
        return new RegexSelector(regex);
    }

    public static RegexSelector regex(String regex, int group) {
        return new RegexSelector(regex, group);
    }

    public static AndSelector and(Selector... selectors) {
        return new AndSelector(selectors);
    }

    public static OrSelector or(Selector... selectors) {
        return new OrSelector(selectors);
    }
}
