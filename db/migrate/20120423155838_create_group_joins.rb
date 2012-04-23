class CreateGroupJoins < ActiveRecord::Migration
  def change
    create_table :group_joins, :force => true do |t|
      t.integer :user_id
      t.integer :group_id
      t.string :role

      t.timestamps
    end

    add_index :group_joins, :user_id, :name => 'group_joins_user_id_ix'
    add_index :group_joins, :group_id, :name => 'group_joins_group_id_ix'
  end
end
