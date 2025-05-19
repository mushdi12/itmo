export enum RegEnum{
    NONE = "None",
    SIGN = "Sign in",
    REG = "Register",
}

export const getRegEnumValue = (value: string): RegEnum => {
    switch (value){
        case "Sign in":
            return RegEnum.SIGN;
        case "Register":
            return RegEnum.REG;
        default:
            return RegEnum.NONE
    }
}