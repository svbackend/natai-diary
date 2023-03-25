import {ChangeEvent, useState} from 'react';
import {useRouter} from 'next/router';
import MainLayout from "../../src/modules/common/components/mainLayout";
import {fetchPostArticles} from "../../src/api/apiComponents";
import {AlertApiError} from "../../src/modules/common/components/alert";

function CreateArticle() {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [slug, setSlug] = useState("");
    const [metaKeywords, setMetaKeywords] = useState("");
    const [metaDescription, setMetaDescription] = useState("");
    const [err, setErr] = useState(null);

    const router = useRouter();

    const handleTitleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setTitle(e.target.value);
    }

    const handleContentChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
        setContent(e.target.value);
    }

    const handleSubmit = async (e: any) => {
        e.preventDefault();

        const req = fetchPostArticles({
            body: {
                translations: [
                    {
                        locale: "en",
                        title: title,
                        content: content,
                        slug: slug,
                        metaKeywords: metaKeywords,
                        metaDescription: metaDescription,
                    }
                ],
                images: [],
            }
        })
            .then((res) => {
                router.push(`/article/${res.articleShortId}/slug`);
            })
            .catch(e => {
                setErr(e);
            })
    }

    return (
        <div>
            <h3 className={"text-3xl my-4"}>New Article</h3>

            {err && (
                <AlertApiError error={err}/>
            )}

            <form className={"flex flex-col"} onSubmit={handleSubmit}>
                <div className="flex flex-row gap-4">
                    <div className="flex flex-col">
                        <div className="flex flex-col">
                            <label htmlFor="title_en">Title (English)</label>
                            <input
                                className={"bg-dark rounded-md px-2 py-1"}
                                type="text"
                                id="title_en"
                                value={title}
                                onChange={(e) => handleTitleChange(e)}
                            />
                        </div>

                        <div className="flex flex-col">
                            <label htmlFor="content_en">Content (English)</label>
                            <textarea
                                className={"bg-dark rounded-md px-2 py-1"}
                                id="content_en"
                                value={content}
                                onChange={(e) => handleContentChange(e)}>

                            </textarea>
                        </div>

                        <div className="flex flex-col">
                            <label htmlFor="slug_en">Slug (English)</label>
                            <input
                                className={"bg-dark rounded-md px-2 py-1"}
                                type="text"
                                id="slug_en"
                                value={slug}
                                onChange={(e) => setSlug(e.target.value)}
                            />
                        </div>

                        <div className="flex flex-col">
                            <label htmlFor="meta_keywords_en">Meta Keywords (English)</label>
                            <input
                                className={"bg-dark rounded-md px-2 py-1"}
                                type="text"
                                id="meta_keywords_en"
                                value={metaKeywords}
                                onChange={(e) => setMetaKeywords(e.target.value)}
                            />
                        </div>

                        <div className="flex flex-col">
                            <label htmlFor="meta_description_en">Meta Description (English)</label>
                            <textarea
                                className={"bg-dark rounded-md px-2 py-1"}
                                id="meta_description_en"
                                value={metaDescription}
                                onChange={(e) => setMetaDescription(e.target.value)}>

                            </textarea>
                        </div>

                    </div>

                </div>

                <div className="flex mt-4">
                    <button type="submit" className={"bg-brand px-4 py-2"}>
                        Submit
                    </button>
                </div>
            </form>
        </div>
    );
}

export default function CreateArticlePage() {
    return (
        <MainLayout>
            <CreateArticle/>
        </MainLayout>
    )
}