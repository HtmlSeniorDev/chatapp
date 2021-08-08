package ru.readme.chatapp.object;

import ru.readme.chatapp.R;

public class UserType {
    public final static int TYPE_USER = 1;
    public final static int TYPE_ADMIN = 2;
    public final static int TYPE_MODER = 4;
    public final static int TYPE_BANNED = 8;
    public final static int TYPE_INVISIBLE = 16;

    public final static int SEX_UNKNOWN = 0;
    public final static int SEX_MAN = 1;
    public final static int SEX_WOMAN = 2;

    public static final int STRING_BANNED = R.string.type_banned;
    public static final int STRING_USER = R.string.type_user;
    public static final int STRING_ADMIN = R.string.type_admin;
    public static final int STRING_MODER = R.string.type_moder;
    public static final int STRING_INVISIBLE = R.string.type_invisible;

    public static int getTypeString(int type) {
        switch (type) {
            case TYPE_ADMIN:
                return STRING_ADMIN;
            case TYPE_BANNED:
                return STRING_BANNED;
            case TYPE_INVISIBLE:
                return STRING_INVISIBLE;
            case TYPE_MODER:
                return STRING_MODER;
            case TYPE_USER:
                return STRING_USER;
            default:
                return STRING_USER;
        }
    }

    public static String getSex(int type) {
        switch (type) {
            case SEX_UNKNOWN:
                return "Неизвестно";
            case SEX_MAN:
                return "Мужской";
            case SEX_WOMAN:
                return "Женский";
            default:
                return "Неизвестно";
        }
    }

}
