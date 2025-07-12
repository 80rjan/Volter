import Lottie from "lottie-react";
import loadingAnimation from "../assets/lottie-dual-ring-animation.json";

export default function Loading({ width = 200, height = 200 }) {
    return (
        <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%'}}>
            <Lottie animationData={loadingAnimation} loop style={{ width, height }} />
        </div>
    )
}
