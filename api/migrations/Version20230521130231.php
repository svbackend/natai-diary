<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

final class Version20230521130231 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql('CREATE TABLE user_order (id SERIAL NOT NULL, user_id UUID NOT NULL, stripe_session_id VARCHAR(255) NOT NULL, status VARCHAR(255) NOT NULL, created_at TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_17EB68C01A314A57 ON user_order (stripe_session_id)');
        $this->addSql('CREATE INDEX IDX_17EB68C0A76ED395 ON user_order (user_id)');
        $this->addSql('COMMENT ON COLUMN user_order.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('COMMENT ON COLUMN user_order.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('CREATE TABLE user_order_feature (id SERIAL NOT NULL, order_id INT NOT NULL, feature VARCHAR(255) NOT NULL, price INT NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE INDEX IDX_723717D8D9F6D38 ON user_order_feature (order_id)');
        $this->addSql('ALTER TABLE user_order ADD CONSTRAINT FK_17EB68C0A76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE user_order_feature ADD CONSTRAINT FK_723717D8D9F6D38 FOREIGN KEY (order_id) REFERENCES user_order (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('ALTER TABLE user_order DROP CONSTRAINT FK_17EB68C0A76ED395');
        $this->addSql('ALTER TABLE user_order_feature DROP CONSTRAINT FK_723717D8D9F6D38');
        $this->addSql('DROP TABLE user_order');
        $this->addSql('DROP TABLE user_order_feature');
    }
}
