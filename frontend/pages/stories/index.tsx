import React from "react";
import MainLayout from "../../src/modules/common/components/mainLayout";


export default function StoriesIndexPage() {
    return (
        <MainLayout>
            <div className={"flex flex-col items-center justify-center"}>
                <div className={"text-center"}>
                    <h1 className={"text-3xl font-bold"}>Stories</h1>

                    <p className={"text-lg mt-4"}>
                        In development...
                    </p>
                </div>
            </div>
        </MainLayout>
    )
}