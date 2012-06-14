class AddUserToAreatag < ActiveRecord::Migration
  def change
    add_column :areatags, :user_id, :integer

  end
end
