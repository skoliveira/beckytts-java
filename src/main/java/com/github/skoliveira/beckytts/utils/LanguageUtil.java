package com.github.skoliveira.beckytts.utils;

import java.util.Locale;
import java.util.stream.Collectors;

public class LanguageUtil {

    // Data extracted from http://download.geonames.org/export/dump/countryInfo.txt
    // 2020/06/28
    private enum DefaultLanguage {
        AD("ca"), // Andorra
        AE("ar"), // United Arab Emirates
        AF("fa"), // Afghanistan
        AG("en"), // Antigua and Barbuda
        AI("en"), // Anguilla
        AL("sq"), // Albania
        AM("hy"), // Armenia
        AN("nl"), // Netherlands Antilles
        AO("pt"), // Angola 
        AR("es"), // Argentina
        AS("en"), // American Samoa
        AT("de"), // Austria
        AU("en"), // Australia
        AW("nl"), // Aruba
        AX("sv"), // Aland Islands
        AZ("az"), // Azerbaijan
        BA("bs"), // Bosnia and Herzegovina
        BB("en"), // Barbados
        BD("bn"), // Bangladesh
        BE("nl"), // Belgium
        BF("fr"), // Burkina Faso
        BG("bg"), // Bulgaria
        BH("ar"), // Bahrain
        BI("fr"), // Burundi
        BJ("fr"), // Benin
        BL("fr"), // Saint Barthelemy
        BM("en"), // Bermuda
        BN("ms"), // Brunei
        BO("es"), // Bolivia
        BQ("nl"), // Bonaire, Saint Eustatius and Saba 
        BR("pt"), // Brazil
        BS("en"), // Bahamas
        BT("dz"), // Bhutan 
        BW("en"), // Botswana
        BY("be"), // Belarus
        BZ("en"), // Belize
        CA("en"), // Canada
        CC("ms"), // Cocos Islands
        CD("fr"), // Democratic Republic of the Congo
        CF("fr"), // Central African Republic
        CG("fr"), // Republic of the Congo
        CH("de"), // Switzerland
        CI("fr"), // Ivory Coast
        CK("en"), // Cook Islands
        CL("es"), // Chile
        CM("en"), // Cameroon
        CN("zh"), // China
        CO("es"), // Colombia
        CS("cu"), // Serbia and Montenegro
        CR("es"), // Costa Rica
        CU("es"), // Cuba
        CV("pt"), // Cabo Verde
        CW("nl"), // Curacao
        CX("en"), // Christmas Island
        CY("el"), // Cyprus
        CZ("cs"), // Czechia
        DE("de"), // Germany
        DJ("fr"), // Djibouti
        DK("da"), // Denmark
        DM("en"), // Dominica
        DO("es"), // Dominican Republic
        DZ("ar"), // Algeria
        EC("es"), // Ecuador
        EE("et"), // Estonia
        EG("ar"), // Egypt
        EH("ar"), // Western Sahara
        ER("aa"), // Eritrea
        ES("es"), // Spain
        ET("am"), // Ethiopia
        FI("fi"), // Finland
        FJ("en"), // Fiji
        FK("en"), // Falkland Islands
        FM("en"), // Micronesia
        FO("fo"), // Faroe Islands
        FR("fr"), // France
        GA("fr"), // Gabon
        GB("en"), // United Kingdom
        GD("en"), // Grenada
        GE("ka"), // Georgia
        GF("fr"), // French Guiana
        GG("en"), // Guernsey
        GH("en"), // Ghana
        GI("en"), // Gibraltar
        GL("kl"), // Greenland
        GM("en"), // Gambia
        GN("fr"), // Guinea
        GP("fr"), // Guadeloupe
        GQ("es"), // Equatorial Guinea
        GR("el"), // Greece
        GS("en"), // South Georgia and the South Sandwich Islands
        GT("es"), // Guatemala
        GU("en"), // Guam
        GW("pt"), // Guinea-Bissau
        GY("en"), // Guyana
        HK("zh"), // Hong Kong
        HN("es"), // Honduras
        HR("hr"), // Croatia
        HT("ht"), // Haiti
        HU("hu"), // Hungary
        ID("id"), // Indonesia
        IE("en"), // Ireland
        IL("he"), // Israel
        IM("en"), // Isle of Man
        IN("en"), // India
        IO("en"), // British Indian Ocean Territory
        IQ("ar"), // Iraq
        IR("fa"), // Iran
        IS("is"), // Iceland
        IT("it"), // Italy
        JE("en"), // Jersey
        JM("en"), // Jamaica
        JO("ar"), // Jordan
        JP("ja"), // Japan
        KE("en"), // Kenya
        KG("ky"), // Kyrgyzstan
        KH("km"), // Cambodia
        KI("en"), // Kiribati
        KM("ar"), // Comoros
        KN("en"), // Saint Kitts and Nevis
        KP("ko"), // North Korea
        KR("ko"), // South Korea
        XK("sq"), // Kosovo
        KW("ar"), // Kuwait
        KY("en"), // Cayman Islands
        KZ("kk"), // Kazakhstan
        LA("lo"), // Laos
        LB("ar"), // Lebanon
        LC("en"), // Saint Lucia
        LI("de"), // Liechtenstein
        LK("si"), // Sri Lanka
        LR("en"), // Liberia
        LS("en"), // Lesotho
        LT("lt"), // Lithuania
        LU("lb"), // Luxembourg
        LV("lv"), // Latvia
        LY("ar"), // Libya
        MA("ar"), // Morocco
        MC("fr"), // Monaco
        MD("ro"), // Moldova
        ME("sr"), // Montenegro
        MF("fr"), // Saint Martin
        MG("fr"), // Madagascar
        MH("mh"), // Marshall Islands
        MK("mk"), // North Macedonia
        ML("fr"), // Mali
        MM("my"), // Myanmar
        MN("mn"), // Mongolia
        MO("zh"), // Macao
        MP("fil"), // Northern Mariana Islands
        MQ("fr"), // Martinique
        MR("ar"), // Mauritania
        MS("en"), // Montserrat
        MT("mt"), // Malta
        MU("en"), // Mauritius
        MV("dv"), // Maldives
        MW("ny"), // Malawi
        MX("es"), // Mexico
        MY("ms"), // Malaysia
        MZ("pt"), // Mozambique
        NA("en"), // Namibia
        NC("fr"), // New Caledonia
        NE("fr"), // Niger
        NF("en"), // Norfolk Island
        NG("en"), // Nigeria
        NI("es"), // Nicaragua
        NL("nl"), // Netherlands
        NO("no"), // Norway
        NP("ne"), // Nepal
        NR("na"), // Nauru
        NU("niu"), // Niue
        NZ("en"), // New Zealand
        OM("ar"), // Oman
        PA("es"), // Panama
        PE("es"), // Peru
        PF("fr"), // French Polynesia
        PG("en"), // Papua New Guinea
        PH("tl"), // Philippines
        PK("ur"), // Pakistan
        PL("pl"), // Poland
        PM("fr"), // Saint Pierre and Miquelon
        PN("en"), // Pitcairn
        PR("en"), // Puerto Rico
        PS("ar"), // Palestinian Territory
        PT("pt"), // Portugal
        PW("pau"), // Palau
        PY("es"), // Paraguay
        QA("ar"), // Qatar
        RE("fr"), // Reunion
        RO("ro"), // Romania
        RS("sr"), // Serbia
        RU("ru"), // Russia
        RW("rw"), // Rwanda
        SA("ar"), // Saudi Arabia
        SB("en"), // Solomon Islands
        SC("en"), // Seychelles
        SD("ar"), // Sudan
        SS("en"), // South Sudan
        SE("sv"), // Sweden
        SG("cmn"), // Singapore
        SH("en"), // Saint Helena
        SI("sl"), // Slovenia
        SJ("no"), // Svalbard and Jan Mayen
        SK("sk"), // Slovakia
        SL("en"), // Sierra Leone
        SM("it"), // San Marino
        SN("fr"), // Senegal
        SO("so"), // Somalia
        SR("nl"), // Suriname
        ST("pt"), // Sao Tome and Principe
        SV("es"), // El Salvador
        SX("nl"), // Sint Maarten
        SY("ar"), // Syria
        SZ("en"), // Eswatini
        TC("en"), // Turks and Caicos Islands
        TD("fr"), // Chad
        TF("fr"), // French Southern Territories
        TG("fr"), // Togo
        TH("th"), // Thailand
        TJ("tg"), // Tajikistan
        TK("tkl"), // Tokelau
        TL("tet"), // Timor Leste
        TM("tk"), // Turkmenistan
        TN("ar"), // Tunisia
        TO("to"), // Tonga
        TR("tr"), // Turkey
        TT("en"), // Trinidad and Tobago
        TV("tvl"), // Tuvalu
        TW("zh"), // Taiwan
        TZ("sw"), // Tanzania
        UA("uk"), // Ukraine
        UG("en"), // Uganda
        UM("en"), // United States Minor Outlying Islands
        US("en"), // United States
        UY("es"), // Uruguay
        UZ("uz"), // Uzbekistan
        VA("la"), // Vatican
        VC("en"), // Saint Vincent and the Grenadines
        VE("es"), // Venezuela
        VG("en"), // British Virgin Islands
        VI("en"), // U.S. Virgin Islands
        VN("vi"), // Vietnam
        VU("bi"), // Vanuatu
        WF("wls"), // Wallis and Futuna
        WS("sm"), // Samoa
        YE("ar"), // Yemen
        YT("fr"), // Mayotte
        ZA("zu"), // South Africa
        ZM("en"), // Zambia
        ZW("en"); // Zimbabwe

        private final String language;

        DefaultLanguage(String language) {
            this.language = language;
        }
        
        public String getLanguage() {
            return this.language;
        }

    }

    private static final int FLAG_SEQUENCE_BASE = 0x1F1A5;

    public static String getAlpha2FromFlag(String flag) {
        return flag.codePoints()
                .mapToObj(code -> String.valueOf(Character.toChars(code - FLAG_SEQUENCE_BASE)))
                .collect(Collectors.joining());
    }

    public static String getFlagFromAlpha2(String alpha2) {
        return alpha2.codePoints()
                .mapToObj(code -> String.valueOf(Character.toChars(FLAG_SEQUENCE_BASE + code)))
                .collect(Collectors.joining());
    }

    public static boolean isISOCountryFlag(String flag) {
        for(String alpha2 : Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2)) {
            if(getFlagFromAlpha2(alpha2).equals(flag))
                return true;
        }
        return false;
    }

    private static boolean hasDefaultLanguage(String flag) {
        for(DefaultLanguage alpha2 : DefaultLanguage.values()) {
            if(alpha2.name().equals(getAlpha2FromFlag(flag)))
                return true;
        }
        return false;
    }

    public static String getLanguageFromFlag(String flag) {
        if(!isISOCountryFlag(flag))
            return null;

        if(!hasDefaultLanguage(flag))
            return null;

        return DefaultLanguage.valueOf(getAlpha2FromFlag(flag)).getLanguage();
    }

    public static void main(String[] args) {

        for(String alpha2 : Locale.getISOCountries()) {
            String language = getLanguageFromFlag(getFlagFromAlpha2(alpha2));
            if(language!=null)
                System.out.println(getFlagFromAlpha2(alpha2) + ' ' + language + '-' + alpha2);
        }

    }

}