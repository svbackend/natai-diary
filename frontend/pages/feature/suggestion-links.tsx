import React from 'react';
import Link from 'next/link';
import MainLayout from "../../src/modules/common/components/mainLayout";

const SuggestionLinks = () => {
    return (
        <MainLayout>
            <div className="bg-whitish dark:bg-darkish py-16 px-4 sm:px-6 lg:px-8">
                <div className="max-w-7xl mx-auto">
                    <div className="lg:text-center">
                        <h2 className="text-3xl font-extrabold tracking-tight text-whitish sm:text-4xl">
                            Additional Mental Health Resources
                        </h2>
                        <p className="mt-4 max-w-2xl text-xl text-dark dark:text-light mx-auto">
                            Gain easy access to curated mental health resources and content tailored to your needs.
                        </p>
                    </div>



                    <div className="text-center mt-16">
                        <p className="text-lg font-medium text-dark">
                            Get easy access to Additional Resources for a one-time payment of $11.20 CAD
                        </p>
                        <div className="mt-6 inline-flex rounded-md shadow">
                            <Link href="/checkout">
                                <button
                                    className="inline-flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-brand hover:bg-brand-alt">
                                    Buy Now
                                </button>
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </MainLayout>
    );
};

export default SuggestionLinks;
