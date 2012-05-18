class AddUserToTagging < ActiveRecord::Migration
  def change
    add_column :taggings, :user_id, :integer

  end
end
