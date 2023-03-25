import {CloudBlogArticleDto} from "../../../src/api/apiSchemas";
import MainLayout from "../../../src/modules/common/components/mainLayout";
import {NotFound} from "../../../src/modules/common/components/NotFound";
import {fetchGetArticles} from "../../../src/api/apiComponents";


export function getServerSideProps(context) {
    const {slug, id} = context.query;
}

export default function ArticleViewPage(props: {article: CloudBlogArticleDto}) {

    const translation = props.article
        .translations
        .find(a => a.locale === "en")

    if (!translation) {
        return <NotFound/>
    }

    const innerHtml = {__html: translation.content}

    return (
        <MainLayout>
            <div className="prose dark:prose-invert">
                <h1>
                    {translation.title}
                </h1>

                <div dangerouslySetInnerHTML={innerHtml}></div>
            </div>
        </MainLayout>
    )
}