import {BASE_URL} from "../utils/const/HttpConst.ts";
import {BACK_API} from "../utils/const/HttpConst.ts";
import {POINTS_EXT} from "../utils/const/HttpConst.ts";

class PointsService {
    public static async getSlice(start: number, limit: number, accessToken: string): Promise<Response>{
        const data = {start: start, limit: limit};
        const finalUrl: string = window.location.origin + BASE_URL + BACK_API + POINTS_EXT;
        const response: Response = await fetch(finalUrl, {
                method: "PATCH",
                headers: {
                    'Authorization' : "Bearer " + accessToken,
                    'Content-Type' : "application/json"
                },
                body: JSON.stringify(data)
            });
        return response;
    }

    public static async createPoint(x: number, y: number, r: number, accessToken: string): Promise<Response>{
        const data = {x: x, y: y, r: r,};
        const finalUrl: string = window.location.origin + BASE_URL + BACK_API + POINTS_EXT;
        const response: Response = await fetch(finalUrl, {
            method: "POST",
            headers: {
                'Content-type': 'application/json',
                'Authorization': "Bearer " + accessToken
            },
            body: JSON.stringify(data)
        });
        return response
    }

    public static async getCountPoints(accessToken: string): Promise<Response>{
        const finalUrl: string = window.location.origin + BASE_URL + BACK_API + POINTS_EXT;
        const response: Response = await fetch(finalUrl, {
            method: "GET",
            headers: {
                'Authorization': "Bearer " + accessToken
            }
        });
        return response;
    }
}

export default PointsService;