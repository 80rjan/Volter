import Lottie from "lottie-react";
import loadingAnimation from "../assets/lottie-dual-ring-animation.json";

interface LoadingProps {
    width?: number;
    height?: number;
}

export default function Loading({ width = 200, height = 200 }: LoadingProps) {
    return (
        <div className="flex justify-center items-center h-full w-full">
            <Lottie animationData={loadingAnimation} loop style={{ width, height }} />
        </div>
    );
}
