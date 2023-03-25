import React, {ChangeEvent, useEffect, useState} from 'react';
import {useRouter} from 'next/router';
import {fetchGetArticlesById, fetchPutArticlesById} from "../../../src/api/apiComponents";
import {CloudBlogArticleDto} from "../../../src/api/apiSchemas";
import {AlertApiError} from "../../../src/modules/common/components/alert";
import MainLayout from "../../../src/modules/common/components/mainLayout";
import NarrowWrapper from "../../../src/modules/common/components/NarrowWrapper";

function UpdateArticle(props: { article: CloudBlogArticleDto }) {
    const trans = props.article.translations[0];

    const [title, setTitle] = useState(trans.title);
    const [content, setContent] = useState(trans.content);
    const [slug, setSlug] = useState(trans.slug);
    const [metaKeywords, setMetaKeywords] = useState(trans.metaKeywords);
    const [metaDescription, setMetaDescription] = useState(trans.metaDescription);
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

        fetchPutArticlesById({
            pathParams: {
                id: props.article.id,
            },
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
                router.push(`/article/${props.article.shortId}/${slug}`);
            })
            .catch(e => {
                setErr(e);
            })
    }

    return (
        <div className={"w-full my-2"}>
            <h3 className={"text-3xl"}>Edit Article</h3>

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

export default function UpdateArticlePage() {
    const router = useRouter();

    const {id} = router.query;

    const articleResponse = fetchGetArticlesById({
        pathParams: {id: id as string},
    })

    const [article, setArticle] = useState<CloudBlogArticleDto | null>(null)

    useEffect(() => {
        if (id) {
            articleResponse
                .then(res => {
                    setArticle(res.article)
                })
                .catch(e => {
                    console.error(e)
                })
        }
    }, [id])

    return (
        <MainLayout>
            <NarrowWrapper>
                {article && (
                    <UpdateArticle article={article}/>
                )}
            </NarrowWrapper>
        </MainLayout>
    )
}