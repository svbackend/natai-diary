import React from "react";
import Link from "next/link";
import {fetchPostFeedback} from "../../src/api/apiComponents";
import MainLayout from "../../src/modules/common/components/mainLayout";

export default function ContactsPage() {
    const [isLoading, setIsLoading] = React.useState(false);

    const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;
        const formData = new FormData(form);
        const feedback = formData.get('feedback');
        const rating = formData.get('rating');

        if (!feedback && !rating) {
            alert('Please provide feedback before submitting');
            return;
        }

        setIsLoading(true)
        const req = fetchPostFeedback({
            body: {
                content: (feedback || "") as string,
                stars: rating ? parseInt(rating as string) : null,
            }
        });

        req
            .then(res => {
                alert('Thank you for your feedback!');
                form.reset();
            })
            .catch(err => {
                console.error(err);
                alert('Something went wrong. Please try again later');
            })
            .finally(() => {
                setIsLoading(false);
            })
    }

    return (
        <MainLayout>
            <div className="mx-auto max-w-md">
                <h1 className="text-4xl font-bold mb-4">Contacts</h1>
                <p className="mb-6">Please feel free to contact us in any of the following ways:</p>
                <ul className="mb-6">
                    <li>
                        <strong>Instagram: </strong>
                        <Link href="https://instagram.com/natai.app" className="text-blue-500" target="_blank"
                              rel="noopener noreferrer">
                            @natai.app
                        </Link>
                    </li>
                    <li>
                        <strong>Twitter: </strong>
                        <Link href="https://twitter.com/natai_app" className="text-blue-500" target="_blank"
                              rel="noopener noreferrer">@natai_app</Link>
                    </li>
                </ul>
                <h2 className="text-2xl font-bold mb-4">Frequently Asked Questions</h2>
                <ul className="mb-6">
                    <li className="mb-2">
                        <h3 className="text-lg font-bold mb-2">
                            How to get personalized recommendations based on my diary notes?
                        </h3>
                        <p className="mb-1">
                            After you have written a few notes (each note should be at least 1 paragraph long, ~100
                            words),
                            you can go to the <Link className={"text-blue-400"} href={"/diary/therapy"}>AI
                            Therapy</Link> page,
                            and your personalized recommendations should be available.
                        </p>
                        <p className={"mb-2"}>
                            If you don't see any recommendations,
                            try to write another diary entry and check again after couple of minutes.
                            If you're sure that you have written enough notes but recommendations still not there,
                            please contact us.
                        </p>
                    </li>
                </ul>

                {/*Feedback form*/}
                <form onSubmit={onSubmit}>
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
                        <div className="inline-flex items-center ml-4">
                            <input type="radio" className="form-radio" name="rating" value="3" id="rating3"/>
                            <label htmlFor="rating3" className="ml-2">3</label>
                        </div>
                        <div className="inline-flex items-center ml-4">
                            <input type="radio" className="form-radio" name="rating" value="4" id="rating4"/>
                            <label htmlFor="rating4" className="ml-2">4</label>
                        </div>
                        <div className="inline-flex items-center ml-4">
                            <input type="radio" className="form-radio" name="rating" value="5" id="rating5"/>
                            <label htmlFor="rating5" className="ml-2">5</label>
                        </div>
                    </div>

                    <div className="mb-6">
                        <label htmlFor="feedback"
                               className="block text-sm font-medium dark:text-gray-300">Feedback</label>
                        <div className="mt-1">
                            <textarea required={true}
                                      minLength={10}
                                      placeholder={"Please provide your feedback here"}
                                      id="feedback" name="feedback" rows={3}
                                      className="text-dark shadow-sm focus:ring-indigo-500 focus:border-indigo-500 mt-1 block w-full sm:text-sm border-gray-300 rounded-md"/>
                        </div>
                    </div>

                    {/*Submit button*/}
                    <div className="flex justify-end">
                        <button type="submit"
                                className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                            Submit
                        </button>
                    </div>
                </form>
            </div>
        </MainLayout>
    )
}