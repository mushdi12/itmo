let clickedPoints = [];
const canvas = document.getElementById("graphCanvas")
const ctx = canvas.getContext("2d");
let hasUserInputR;
const baseScaling = (Math.min(canvas.width, canvas.height) / 3);
const xCenter = canvas.width / 2;
const yCenter = canvas.height / 2;


window.onload = function () {
        //setCanvasDPI();
        drawGraph(3);
        //drawPoint();
    }
    const rOptions = document.getElementsByName("rSpinner");
    // Функция для проверки и обработки изменения значения R
    function checkSelectedR() {
        const spinner = PF('spinnerWidget');
        if (spinner) {
            const rValue = spinner.getValue();
            if (rValue) {
                console.log(`Выбрано значение R: ${rValue}`);
                hasUserInputR = rValue;
                drawGraph(rValue);
                //drawPoint();
            } else {
                console.error('Значение R не выбрано');
            }
        } else {
            console.error('Элемент spinner не найден');
        }
    }

canvas.addEventListener("click", handleClick);

function handleClick(event) {
    event.preventDefault();
    if (hasUserInputR) {
        //const dpi = window.devicePixelRatio;
        let x = event.offsetX;
        let y = event.offsetY;
        x -=  xCenter;
        y -= yCenter;
        x /= baseScaling;
        y /= baseScaling;
        let scaledX = (x * hasUserInputR).toFixed(3);
        let scaledY = (-y * hasUserInputR).toFixed(3);

        // Используйте полный идентификатор
        document.getElementById('pointForm:hiddenX').value = scaledX;
        document.getElementById('pointForm:hiddenY').value = scaledY;
        document.getElementById('pointForm:hiddenR').value = hasUserInputR;

        jsf.ajax.request(document.getElementById('pointForm:check'), null, {
            execute: 'pointForm:hiddenX pointForm:hiddenY pointForm:hiddenR',
            render: 'pointForm:result',
            onevent: function(data) {
                if (data.status === 'success') {
                    drawPoint(); // Отрисовываем точку после успешного обновления
                }
            }
        });
    } else {
        showToastError("Выберите значение hasUserInputR");
        return;
    }
}


    let dynamicScalingFactor;

    // function setCanvasDPI() {
    //     let dpi = window.devicePixelRatio;
    //     let canvasElement = document.getElementById('graphCanvas');
    //     let style = {
    //         height() {
    //             return +getComputedStyle(canvasElement).getPropertyValue('height').slice(0, -2);
    //         },
    //         width() {
    //             return +getComputedStyle(canvasElement).getPropertyValue('width').slice(0, -2);
    //         }
    //     };
    //
    //     canvasElement.setAttribute('width', style.width() * dpi);
    //     canvasElement.setAttribute('height', style.height() * dpi);
    // }

    function drawGraph(R) {
        console.log("Граф рисуется");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.strokeStyle = "black";
        ctx.lineWidth = "2";

        ctx.moveTo(canvas.width - 10, yCenter - 5);
        ctx.lineTo(canvas.width, yCenter);
        ctx.lineTo(canvas.width - 10, yCenter + 5);
        ctx.fillStyle = "black";
        ctx.fill();
        ctx.closePath();

        ctx.moveTo(xCenter - 5, 10);
        ctx.lineTo(xCenter, 0);
        ctx.lineTo(xCenter + 5, 10);
        ctx.fillStyle = "black";
        ctx.fill();
        ctx.closePath();

        ctx.beginPath();
        ctx.arc(xCenter, yCenter, baseScaling/2, Math.PI/2, Math.PI, false);
        ctx.lineTo(xCenter, yCenter);
        ctx.closePath();

        ctx.fillStyle = "#D8BFD8";
        ctx.fill();

        ctx.fillRect(xCenter, yCenter - baseScaling, baseScaling, baseScaling);

        ctx.beginPath();
        ctx.moveTo(xCenter, yCenter);
        ctx.lineTo(xCenter - baseScaling / 2, yCenter);
        ctx.lineTo(xCenter, yCenter - baseScaling/2);
        ctx.closePath();

        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(0, yCenter);
        ctx.lineTo(canvas.width, yCenter);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(xCenter, 0);
        ctx.lineTo(xCenter, canvas.height);
        ctx.stroke();

        ctx.textAlign = "left";
        ctx.textBaseline = "middle";
        ctx.fillStyle = "black";
        ctx.font = "14px Arial";

        ctx.fillText(-hasUserInputR, xCenter + 7, yCenter + baseScaling);
        ctx.fillText(hasUserInputR, xCenter + 7, yCenter - baseScaling);
        ctx.fillText(-hasUserInputR / 2, xCenter + 7, yCenter + baseScaling / 2);
        ctx.fillText(hasUserInputR / 2, xCenter + 7, yCenter - baseScaling / 2);

        ctx.fillText(-hasUserInputR, xCenter - baseScaling, yCenter + 10);
        ctx.fillText(hasUserInputR, xCenter + baseScaling, yCenter + 10);
        ctx.fillText(-hasUserInputR / 2, xCenter - baseScaling / 2, yCenter + 10);
        ctx.fillText(hasUserInputR / 2, xCenter + baseScaling / 2, yCenter + 10);

        if (hasUserInputR) {
            // console.log("drawing points");
            drawPoint();
        }
    }

    // function drawAxis(context, fromX, fromY, toX, toY) {
    //     const headLength = 10;
    //     const angle = Math.atan2(toY - fromY, toX - fromX);
    //
    //     context.beginPath();
    //     context.moveTo(fromX, fromY);
    //     context.lineTo(toX, toY);
    //     context.lineTo(toX - headLength * Math.cos(angle - Math.PI / 6), toY - headLength * Math.sin(angle - Math.PI / 6));
    //     context.moveTo(toX, toY);
    //     context.lineTo(toX - headLength * Math.cos(angle + Math.PI / 6), toY - headLength * Math.sin(angle + Math.PI / 6));
    //     context.stroke();
    // }

    function drawPoint() {
        const table = document.querySelector('.result');
        if (table) {
            const observer = new MutationObserver(() => {
                updatePointsFromTable();
            });

            observer.observe(table, { childList: true, subtree: true });

            // Initial drawing
            updatePointsFromTable();
        }
    }

    function updatePointsFromTable() {
        const table = document.querySelector('.result');
        if (table) {
            const rows = table.querySelectorAll("tbody tr");
            clickedPoints = []; // Clear previous points
            rows.forEach(row => {
                const xElement = row.querySelector('[id$="xValue"]');
                const yElement = row.querySelector('[id$="yValue"]');
                const resElement = row.querySelector('[id$="resultValue"]');
                const rElement = row.querySelector('[id$="rValue"]')

                if (xElement && yElement && resElement) {
                    const x = parseFloat(xElement.textContent.trim());
                    const y = parseFloat(yElement.textContent.trim());
                    const res = resElement.textContent.trim();
                    //const hasUserInputR = parseFloat(rElement.textContent.trim());

                    if (!isNaN(x) && !isNaN(y)) {
                        const dpi = window.devicePixelRatio;
                        ctx.fillStyle = (res === "true") ? "green" : "red";
                        ctx.beginPath();
                        let scaleX = xCenter + (x / hasUserInputR) * baseScaling;
                        let scaleY = yCenter - (y / hasUserInputR) * baseScaling;
                        ctx.arc(scaleX, scaleY, 5, 0, Math.PI * 2);
                        ctx.fill();
                    } else {
                        console.error('Некорректные данные для точки:', { x, y, res });
                    }
                } else {
                    console.error('Не найдены элементы с данными:', { xElement, yElement, resElement });
                }
            });
        } else {
            console.error('Таблица с классом .result не найдена');
        }
    }

    async function drawAllPoints() {
        console.log("Точки рисуются");
        clickedPoints.forEach(point => {
            ctx.fillStyle = point.c;
            ctx.beginPath();
            ctx.arc(point.x, point.y, 5, 0, Math.PI * 2);
            //ctx.fillStyle = "blue"; // Цвет точки
            ctx.fill();
        });
    }

