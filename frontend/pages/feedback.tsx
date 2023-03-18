import React from "react";
import MainLayout from "../src/modules/common/components/mainLayout";
import Link from "next/link";

export default function FeedbackPage() {
    return (
        <MainLayout>
            <div className="container max-w-lg mx-auto px-4 py-8">
                <h1 className="text-4xl font-bold mb-4">Feedback/Support</h1>
                <p className="mb-6">Please feel free to contact us in any of the following ways:</p>
                <ul className="mb-6">
                    <li>
                        <strong>Instagram: </strong>
                        <Link href="https://instagram.com/natai.app" className="text-blue-500" target="_blank" rel="noopener noreferrer">
                            @natai.app
                        </Link>
                    </li>
                    {/*<li>*/}
                    {/*    <strong>Twitter:</strong>*/}
                    {/*    <Link href="https://twitter.com/svbackend" className="text-blue-500" target="_blank" rel="noopener noreferrer">@svbackend</Link>*/}
                    {/*</li>*/}
                </ul>
                <h2 className="text-2xl font-bold mb-4">Frequently Asked Questions</h2>
                <ul className="mb-6">
                    <li className="mb-2">
                        <h3 className="text-lg font-bold mb-2">Question 1</h3>
                        <p className="mb-2">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod
                            tempor incididunt ut labore et dolore magna aliqua.</p>
                    </li>
                </ul>
                <h2 className="text-2xl font-bold mb-4">Send Feedback</h2>
                <p className="mb-4">Please rate your experience with us:</p>
                <div className="mb-6">
                    <div className="inline-flex items-center">
                        <input type="radio" className="form-radio" name="rating" value="1" id="rating1"/>
                        <label htmlFor="rating1" className="ml-2">1</label>
                    </div>
                    <div className="inline-flex items-center ml-4">
                        <input type="radio" className="form-radio" name="rating" value="2" id="rating2"/>
                        <label htmlFor="rating2" className="ml-2">2</label>
                    </div>
                </div>
            </div>
        </MainLayout>
    )
}