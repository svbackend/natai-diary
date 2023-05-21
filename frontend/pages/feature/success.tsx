import MainLayout from "../../src/modules/common/components/mainLayout";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import React from "react";
import Link from "next/link";

/**
 * Success page (user redirected here after successful payment for one of the features)
 */
export default function SuccessPage() {
    return (
        <MainLayout>
            <NarrowWrapper>
                <div className="flex flex-col items-center justify-center py-2 text-center">
                    <h1 className="text-4xl font-bold text-gray-900 dark:text-light">Payment processed successfully!</h1>
                    <p className="mt-3 text-2xl">
                        Thank you for your purchase! Your payment has been successfully processed.
                    </p>
                    <p className="mt-3 text-xl">
                        You should receive access to the feature you purchased within couple of moments.
                    </p>

                    <div className="mt-8">
                        <Link href="/diary" className="text-blue-500 hover:text-blue-700">
                            Go back to diary
                        </Link>
                    </div>
                </div>
            </NarrowWrapper>
        </MainLayout>
    );
}
