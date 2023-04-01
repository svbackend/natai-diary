<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230401005826 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql('CREATE SEQUENCE user_log_id_seq INCREMENT BY 1 MINVALUE 1 START 1');
        $this->addSql('CREATE TABLE suggestion_context (id UUID NOT NULL, suggestion_id UUID DEFAULT NULL, input TEXT NOT NULL, context TEXT NOT NULL, usage JSON NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_8D87C60AA41BB822 ON suggestion_context (suggestion_id)');
        $this->addSql('COMMENT ON COLUMN suggestion_context.id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN suggestion_context.suggestion_id IS \'(DC2Type:uuid)\'');
        $this->addSql('CREATE TABLE user_log (id INT NOT NULL, user_id UUID NOT NULL, event VARCHAR(255) NOT NULL, data JSON NOT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_6429094EA76ED395 ON user_log (user_id)');
        $this->addSql('COMMENT ON COLUMN user_log.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN user_log.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('ALTER TABLE suggestion_context ADD CONSTRAINT FK_8D87C60AA41BB822 FOREIGN KEY (suggestion_id) REFERENCES suggestion (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE user_log ADD CONSTRAINT FK_6429094EA76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');

        $this->addSql('ALTER TABLE users ADD created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT NOW()');
        $this->addSql('ALTER TABLE users ALTER created_at DROP DEFAULT');
        $this->addSql('COMMENT ON COLUMN users.created_at IS \'(DC2Type:datetime_immutable)\'');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP SEQUENCE user_log_id_seq CASCADE');
        $this->addSql('ALTER TABLE suggestion_context DROP CONSTRAINT FK_8D87C60AA41BB822');
        $this->addSql('ALTER TABLE user_log DROP CONSTRAINT FK_6429094EA76ED395');
        $this->addSql('DROP TABLE suggestion_context');
        $this->addSql('DROP TABLE user_log');
        $this->addSql('ALTER TABLE "users" DROP created_at');
    }
}
