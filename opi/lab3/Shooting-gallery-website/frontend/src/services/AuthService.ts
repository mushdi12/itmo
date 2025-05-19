import FormState from "../models/FormState";
import {AUTH_EXT, BACK_API, BASE_URL} from "../utils/const/HttpConst.ts";

export default class AuthService{
    public static async refresh(): Promise<{username: string | null, token: string | null}> {
        const finalUrl: string = window.location.origin + BASE_URL + BACK_API + AUTH_EXT;
        const response: Response = await fetch(finalUrl, {
            method: "PATCH"
        });
        const token = await this.handleRespUser(response);
        const username = response.headers.get("X-Username");
        return {username, token};
    }

    public static async register(form: FormState): Promise<string | null> {
        const finalUrl: string = window.location.origin + BASE_URL + BACK_API + AUTH_EXT;
        const response: Response = await fetch(finalUrl,{
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(form)
        });
        return await AuthService.handleRespUser(response);
    }

    public static async signIn(form: FormState): Promise<string | null> {
        const finalUrl: string = window.location.origin + BASE_URL + BACK_API + AUTH_EXT;
        const response: Response = await fetch(finalUrl, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(form)
        });
        return await AuthService.handleRespUser(response);
    }

    public static async logout(): Promise<boolean> {
        const finalUrl: string = window.location.origin + BASE_URL + BACK_API + AUTH_EXT;
        const response: Response = await fetch(finalUrl, {
            method: "DELETE",
        });
        console.log("logoutResp", response);
        return response.ok;
    }

    public static async handleRespUser(response: Response): Promise<string | null> {
        if (response.ok){
            console.log("Регистрация прошла успешно!!!");
            const respData = await response.json();
            const accessToken: string = respData.result;
            if (accessToken){
                return accessToken;
            } else {
                return null;
            }
        } else if (response.status == 409) {
            return "";
        }else {
            return null;
        }
    }
}