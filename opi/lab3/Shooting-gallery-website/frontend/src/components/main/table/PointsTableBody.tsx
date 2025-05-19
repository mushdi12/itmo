import {FC} from "react";
import Point from "../../../models/Point.ts";

const PointsTableBody: FC<{points: (Point | null)[] | null}> = ({points}) => {
    return <tbody>
    {
        points != null && points[0] == null && <tr>
            <td>
                {"none"}
            </td>
            <td>
                {"none"}
            </td>
            <td>
                {"none"}
            </td>
            <td>
                {"none"}
            </td>
        </tr>
    }
    {
        points != null && points.map(point => {
            if (point != null) {
                return <tr>
                    <td>
                        {point.x.toLocaleString("en-US", {maximumFractionDigits: 3})}
                    </td>
                    <td>
                        {point.y.toLocaleString("en-US", {maximumFractionDigits: 3})}
                    </td>
                    <td>
                        {point.r}
                    </td>
                    <td>
                        {point.hitting ? "hit": "slip"}
                    </td>
                </tr>;
            }
            }
        )
    }
    </tbody>;
}

export default PointsTableBody;